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
import com.squareup.kotlinpoet.KModifier
import me.omico.elucidator.KtFileScope
import me.omico.elucidator.TypeScope
import me.omico.elucidator.addFunction
import me.omico.elucidator.addObjectType
import me.omico.elucidator.addParameter
import me.omico.elucidator.addProperty
import me.omico.elucidator.initializer
import me.omico.elucidator.ktFile
import me.omico.elucidator.modifier
import me.omico.elucidator.returnStatement
import me.omico.elucidator.superclass
import me.omico.elucidator.writeTo
import me.omico.gradm.GRADM_DEPENDENCY_PACKAGE_NAME
import me.omico.gradm.VersionsMeta
import me.omico.gradm.internal.YamlDocument
import me.omico.gradm.internal.config.Dependency
import me.omico.gradm.internal.config.collectAllDependencies
import org.gradle.api.internal.artifacts.dependencies.DefaultExternalModuleDependency
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

internal val CodegenDependency.hasDependency: Boolean
    get() = group.isNotEmpty() && artifact.isNotEmpty()

internal val CodegenDependency.hasSubDependencies: Boolean
    get() = subDependencies.isNotEmpty()

internal typealias CodegenDependencies = TreeMap<String, CodegenDependency>

internal fun CodeGenerator.generateDependenciesSourceFiles() {
    dependencies.forEach { (name, dependency) ->
        ktFile(GRADM_DEPENDENCY_PACKAGE_NAME, name.capitalize()) {
            addSuppressWarningTypes()
            addGradmComment()
            addDependencyObjects(name, dependency)
            writeTo(generatedSourcesDirectory)
        }
    }
}

internal fun YamlDocument.createCodegenDependencies(versionsMeta: VersionsMeta): CodegenDependencies =
    CodegenDependencies().apply {
        this@createCodegenDependencies.collectAllDependencies().forEach { dependency ->
            addDependency(versionsMeta, dependency)
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

private fun KtFileScope.addDependencyObjects(name: String, dependency: CodegenDependency) {
    if (dependency.hasParent && !dependency.hasSubDependencies) return
    addObjectType(name.capitalize()) {
        addDependencySuperClass(dependency)
        addDependencies(name, dependency)
    }
    dependency.subDependencies.forEach { (subName, subDependency) ->
        addDependencyObjects("${name.capitalize()}${subName.capitalize()}", subDependency)
    }
}

private fun TypeScope.addDependencySuperClass(dependency: CodegenDependency) {
    if (!dependency.hasDependency) return
    superclass<DefaultExternalModuleDependency> {
        addParameter("%S", dependency.group)
        addParameter("%S", dependency.artifact)
        addParameter("%S", dependency.version ?: "+")
    }
    addFunction("invoke") {
        modifier(KModifier.OPERATOR)
        addParameter<String>("version")
        returnStatement<String>("\"${dependency.module}:\$version\"")
    }
}

private fun TypeScope.addDependencies(name: String, dependency: CodegenDependency): Unit =
    dependency.subDependencies.forEach { (subName, subDependency) ->
        when {
            subDependency.hasSubDependencies ->
                addDependencyProperty(subName, "${name.capitalize()}${subName.capitalize()}")
            else -> addDependency(subName, subDependency)
        }
    }

private fun TypeScope.addDependencyProperty(propertyName: String, className: String): Unit =
    addProperty(propertyName, ClassName(GRADM_DEPENDENCY_PACKAGE_NAME, className)) {
        initializer(className)
    }

private fun TypeScope.addDependency(name: String, dependency: CodegenDependency) {
    if (!dependency.hasDependency) return
    addProperty<String>(name.camelCase()) {
        when {
            dependency.noSpecificVersion -> {
                modifier(KModifier.CONST)
                initializer("\"${dependency.module}\"")
            }
            else -> initializer("${name.camelCase()}(\"${dependency.version}\")")
        }
    }
    addFunction(name.camelCase()) {
        addParameter<String>("version")
        returnStatement<String>("\"${dependency.module}:\$version\"")
    }
}
