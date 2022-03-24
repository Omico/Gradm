/*
 * Copyright 2022 Omico
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
import me.omico.gradm.internal.VersionsMeta
import me.omico.gradm.internal.config.Dependency
import me.omico.gradm.internal.config.alias
import org.gradle.api.internal.artifacts.dependencies.DefaultExternalModuleDependency
import java.util.Locale

internal typealias SubDependencies = HashMap<String, CodegenDependency>

internal data class CodegenDependency(
    val name: String,
    var library: CodegenLibrary?,
    val subDependencies: SubDependencies,
)

internal val CodegenDependency.hasSubDependencies: Boolean
    get() = subDependencies.isNotEmpty()

internal fun CodegenDependency.toFileSpec(): FileSpec =
    FileSpec.builder(GRADM_DEPENDENCY_PACKAGE_NAME, name)
        .addSuppressWarningTypes()
        .addGradmComment()
        .addDependencyObjects(this@toFileSpec)
        .build()

internal fun FileSpec.Builder.addDslProperty(propertyName: String, receivers: Array<ClassName>): FileSpec.Builder =
    apply {
        receivers.forEach { className ->
            PropertySpec
                .builder(
                    propertyName.lowercase(Locale.getDefault()),
                    ClassName(GRADM_DEPENDENCY_PACKAGE_NAME, propertyName)
                )
                .receiver(className)
                .getter(
                    FunSpec.getterBuilder()
                        .addStatement("return $propertyName", ClassName(GRADM_DEPENDENCY_PACKAGE_NAME, propertyName))
                        .build()
                )
                .build()
                .also(::addProperty)
        }
    }

internal fun VersionsMeta.toCodegenDependency(dependency: Dependency): CodegenDependency =
    CodegenDependency(dependency.name).apply {
        dependency.libraries.forEach { library ->
            addLibrary(library.alias(), library.toCodegenLibrary(this@toCodegenDependency))
        }
    }

private fun CodegenDependency.addLibrary(alias: String, library: CodegenLibrary): Unit =
    when {
        alias.contains(".") -> {
            val subName = alias.substringBefore(".")
            val subAlias = alias.substringAfter(".")
            val subDependencyName = "${name.capitalize()}${subName.capitalize()}"
            getOrCreateSubDependency(subName, subDependencyName).addLibrary(subAlias, library)
        }
        else -> getOrCreateSubDependency(alias, "${name.capitalize()}${alias.capitalize()}").library = library
    }

private fun CodegenDependency.getOrCreateSubDependency(
    name: String,
    subDependencyName: String,
    library: CodegenLibrary? = null,
): CodegenDependency =
    subDependencies[name] ?: CodegenDependency(subDependencyName, library).also { subDependencies[name] = it }

private fun CodegenDependency(name: String, library: CodegenLibrary? = null): CodegenDependency =
    CodegenDependency(
        name = name,
        library = library,
        subDependencies = hashMapOf(),
    )

private fun FileSpec.Builder.addDependencyObjects(dependency: CodegenDependency): FileSpec.Builder =
    apply {
        if (!dependency.hasSubDependencies) return@apply
        TypeSpec.objectBuilder(dependency.name)
            .addDependencySuperClass(dependency)
            .addSubDependencies(dependency)
            .build()
            .also(::addType)
        dependency.subDependencies.values.sortedBy { it.name }.forEach { subDependency ->
            addDependencyObjects(subDependency)
        }
    }

private fun TypeSpec.Builder.addDependencySuperClass(dependency: CodegenDependency): TypeSpec.Builder =
    apply {
        if (!dependency.hasSubDependencies) return@apply
        val library = dependency.library ?: return@apply
        superclass(DefaultExternalModuleDependency::class)
        addSuperclassConstructorParameter("%S", library.group)
        addSuperclassConstructorParameter("%S", library.artifact)
        addSuperclassConstructorParameter("%S", library.version)
        FunSpec.builder("invoke")
            .addModifiers(KModifier.OPERATOR)
            .addParameter("version", String::class)
            .returns(String::class)
            .addStatement("return \"${library.module}:\$version\"", String::class)
            .build()
            .also(::addFunction)
    }

private fun TypeSpec.Builder.addSubDependencies(dependency: CodegenDependency): TypeSpec.Builder =
    apply {
        dependency.subDependencies.toSortedMap().forEach { (propertyName, subDependency) ->
            when {
                subDependency.hasSubDependencies -> addSubDependencyProperty(propertyName, subDependency.name)
                else -> addLibrary(propertyName, subDependency.library)
            }
        }
    }

private fun TypeSpec.Builder.addSubDependencyProperty(propertyName: String, dependencyName: String): TypeSpec.Builder =
    PropertySpec.builder(propertyName, ClassName(GRADM_DEPENDENCY_PACKAGE_NAME, dependencyName))
        .initializer(dependencyName)
        .build()
        .let(::addProperty)
