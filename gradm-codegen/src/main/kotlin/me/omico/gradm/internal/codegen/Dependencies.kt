/*
 * Copyright 2022-2023 Omico
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.omico.gradm.internal.codegen

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import me.omico.gradm.GRADM_DEPENDENCY_PACKAGE_NAME
import me.omico.gradm.GradmExperimentalConfiguration
import me.omico.gradm.VersionsMeta
import me.omico.gradm.internal.YamlDocument
import me.omico.gradm.internal.config.Dependency
import me.omico.gradm.internal.config.collectAllDependencies
import org.gradle.api.internal.artifacts.dependencies.DefaultExternalModuleDependency
import java.nio.file.Path
import java.util.TreeMap

internal data class CodegenDependency(
    val hasParent: Boolean = false,
    val group: String = "",
    val artifact: String = "",
    val version: String? = null,
    val subDependencies: CodegenDependencies = CodegenDependencies(),
) {
    val module: String by lazy { "$group:$artifact" }
    val noSpecificVersion: Boolean by lazy { version.isNullOrBlank() }
}

internal val CodegenDependency.hasDependency
    get() = group.isNotEmpty() && artifact.isNotEmpty()

internal val CodegenDependency.hasSubDependencies
    get() = subDependencies.isNotEmpty()

internal typealias CodegenDependencies = TreeMap<String, CodegenDependency>

fun generateDependenciesSourceFiles(
    generatedSourcesDirectory: Path,
    document: YamlDocument,
    versionsMeta: VersionsMeta,
) {
    val dependencies = document.createCodegenDependencies(versionsMeta)
    dependencies.forEach { (name, dependency) ->
        dependency.toFileSpec(name).writeTo(generatedSourcesDirectory)
    }
    dependencies.toDslFileSpec().writeTo(generatedSourcesDirectory)
}

private fun YamlDocument.createCodegenDependencies(versionsMeta: VersionsMeta): CodegenDependencies =
    CodegenDependencies().apply {
        this@createCodegenDependencies.collectAllDependencies().forEach { dependency ->
            addDependency(versionsMeta, dependency)
        }
    }

private fun CodegenDependency.toFileSpec(name: String): FileSpec =
    FileSpec.builder(GRADM_DEPENDENCY_PACKAGE_NAME, name.capitalize())
        .addSuppressWarningTypes()
        .addGradmComment()
        .addDependencyObjects(name, this@toFileSpec)
        .build()

private fun CodegenDependencies.toDslFileSpec(): FileSpec =
    FileSpec.builder("", "Dependencies")
        .addSuppressWarningTypes()
        .addGradmComment()
        .apply {
            keys.forEach { name ->
                addDslProperty(
                    name = name,
                    receivers = mutableSetOf<ClassName>().apply {
                        // For normal module, rollback to the DependencyHandler to prevent naming conflict.
                        ClassName("org.gradle.api.artifacts.dsl", "DependencyHandler").let(::add)
                        if (GradmExperimentalConfiguration.kotlinMultiplatformSupport) {
                            ClassName("org.gradle.api", "Project").let(::add)
                        }
                    },
                )
            }
        }
        .build()

internal fun FileSpec.Builder.addDslProperty(name: String, receivers: Set<ClassName>): FileSpec.Builder =
    apply {
        receivers.forEach { className ->
            PropertySpec
                .builder(name, ClassName(GRADM_DEPENDENCY_PACKAGE_NAME, name.capitalize()))
                .receiver(className)
                .getter(
                    FunSpec.getterBuilder()
                        .addStatement(
                            "return ${name.capitalize()}",
                            ClassName(GRADM_DEPENDENCY_PACKAGE_NAME, name.capitalize()),
                        )
                        .build(),
                )
                .build()
                .also(::addProperty)
        }
    }

private fun CodegenDependencies.addDependency(
    versionsMeta: VersionsMeta,
    dependency: Dependency,
    hasParent: Boolean = false,
) {
    val alias = dependency.alias
    when {
        alias.contains(".") ->
            getOrPut(alias.substringBefore("."), ::CodegenDependency).subDependencies
                .addDependency(
                    versionsMeta = versionsMeta,
                    dependency = dependency.copy(alias = alias.substringAfter(".")),
                    hasParent = true,
                )
        else ->
            getOrDefault(alias, CodegenDependency())
                .copy(
                    hasParent = hasParent,
                    group = dependency.group,
                    artifact = dependency.artifact,
                    version = when {
                        dependency.noSpecificVersion -> versionsMeta[dependency.module]
                        else -> versionsMeta.resolveVariable(dependency.module, dependency.version)
                    },
                )
                .also { put(alias, it) }
    }
}

private fun FileSpec.Builder.addDependencyObjects(name: String, dependency: CodegenDependency): FileSpec.Builder =
    apply {
        if (dependency.hasParent && !dependency.hasSubDependencies) return@apply
        TypeSpec.objectBuilder(name.capitalize())
            .addDependencySuperClass(dependency)
            .addDependencies(name, dependency)
            .build()
            .also(::addType)
        dependency.subDependencies.forEach { (subName, subDependency) ->
            addDependencyObjects("${name.capitalize()}${subName.capitalize()}", subDependency)
        }
    }

private fun TypeSpec.Builder.addDependencySuperClass(dependency: CodegenDependency): TypeSpec.Builder =
    apply {
        if (!dependency.hasDependency) return@apply
        superclass(DefaultExternalModuleDependency::class)
        addSuperclassConstructorParameter("%S", dependency.group)
        addSuperclassConstructorParameter("%S", dependency.artifact)
        addSuperclassConstructorParameter("%S", dependency.version ?: "+")
        FunSpec.builder("invoke")
            .addModifiers(KModifier.OPERATOR)
            .addParameter("version", String::class)
            .returns(String::class)
            .addStatement("return \"${dependency.module}:\$version\"", String::class)
            .build()
            .also(::addFunction)
    }

private fun TypeSpec.Builder.addDependencies(name: String, dependency: CodegenDependency): TypeSpec.Builder =
    apply {
        dependency.subDependencies.forEach { (subName, subDependency) ->
            when {
                subDependency.hasSubDependencies ->
                    addDependencyProperty(subName, "${name.capitalize()}${subName.capitalize()}")
                else -> addDependency(subName, subDependency)
            }
        }
    }

private fun TypeSpec.Builder.addDependencyProperty(propertyName: String, className: String): TypeSpec.Builder =
    PropertySpec.builder(propertyName, ClassName(GRADM_DEPENDENCY_PACKAGE_NAME, className))
        .initializer(className)
        .build()
        .let(::addProperty)

private fun TypeSpec.Builder.addDependency(name: String, dependency: CodegenDependency): TypeSpec.Builder =
    apply {
        if (!dependency.hasDependency) return@apply
        PropertySpec.builder(name.camelCase(), String::class)
            .apply {
                when {
                    dependency.noSpecificVersion -> {
                        addModifiers(KModifier.CONST)
                        initializer("\"${dependency.module}\"")
                    }
                    else -> initializer("${name.camelCase()}(\"${dependency.version}\")")
                }
            }
            .build()
            .also(::addProperty)
        FunSpec.builder(name.camelCase())
            .addParameter("version", String::class)
            .returns(String::class)
            .addStatement("return \"${dependency.module}:\$version\"", String::class)
            .build()
            .also(::addFunction)
    }
