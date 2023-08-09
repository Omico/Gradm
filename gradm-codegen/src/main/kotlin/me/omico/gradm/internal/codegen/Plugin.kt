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
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import me.omico.elucidator.FunctionScope
import me.omico.elucidator.addClass
import me.omico.elucidator.addComment
import me.omico.elucidator.addFunction
import me.omico.elucidator.addIfStatement
import me.omico.elucidator.addLambdaStatement
import me.omico.elucidator.addParameter
import me.omico.elucidator.addStatement
import me.omico.elucidator.addSuperinterface
import me.omico.elucidator.ktFile
import me.omico.elucidator.modifier
import me.omico.elucidator.writeTo
import me.omico.gradm.GRADM_DEPENDENCY_PACKAGE_NAME
import me.omico.gradm.GRADM_PACKAGE_NAME
import me.omico.gradm.GradmExperimentalConfiguration
import me.omico.gradm.GradmGeneratedPluginType
import me.omico.gradm.VersionsMeta
import me.omico.gradm.internal.YamlDocument
import me.omico.gradm.internal.config.plugins
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.initialization.Settings
import org.gradle.api.plugins.ExtensionAware
import java.nio.file.Path

internal fun CodeGenerator.generatePluginSourceFile(): Unit =
    generatePluginSourceFile<ExtensionAware>(
        generatedSourcesDirectory = generatedSourcesDirectory,
        type = GradmGeneratedPluginType.General,
        overrideApplyFunctionBuilder = {
            addIfStatement {
                start("target is %T", Settings::class) {
                    declarePluginsInSettings(gradmConfigurationDocument, versionsMeta)
                    declareRepositoriesInSettings(gradmConfigurationDocument)
                    declareDependenciesInSettings(dependencies)
                    declareVersionsInSettings()
                }
            }
            addIfStatement {
                start("target is %T", Project::class) {
                    declareDependenciesInProject(dependencies)
                    declareVersionsInProject()
                }
            }
        },
    )

private inline fun <reified T> generatePluginSourceFile(
    generatedSourcesDirectory: Path,
    type: GradmGeneratedPluginType,
    noinline overrideApplyFunctionBuilder: FunctionScope.() -> Unit = {},
) {
    ktFile(type.packageName, type.className) {
        addSuppressWarningTypes(types = defaultSuppressWarningTypes + "UnstableApiUsage")
        addGradmComment()
        addClass(type.className) {
            addSuperinterface(Plugin::class.parameterizedBy(T::class))
            addFunction("apply") {
                modifier(KModifier.OVERRIDE)
                addParameter<T>("target")
                apply(overrideApplyFunctionBuilder)
            }
        }
        writeTo(generatedSourcesDirectory)
    }
}

private fun FunctionScope.declarePluginsInSettings(document: YamlDocument, versionsMeta: VersionsMeta): Unit =
    addLambdaStatement("target.pluginManagement.plugins") {
        document.plugins
            .sortedBy { plugin -> plugin.id }
            .forEach { plugin ->
                val version = versionsMeta.resolveVariable(plugin.module, plugin.version)
                addStatement("id(\"${plugin.id}\").version(\"${version}\").apply(false)")
            }
    }

private fun FunctionScope.declareRepositoriesInSettings(document: YamlDocument): Unit =
    addLambdaStatement("target.dependencyResolutionManagement.repositories") {
        addDeclaredRepositoryStatements(document)
    }

private fun FunctionScope.declareDependenciesInSettings(dependencies: CodegenDependencies): Unit =
    addLambdaStatement("target.gradle.rootProject") {
        addLambdaStatement("allprojects") {
            declareDependencies(dependencies)
        }
    }

private fun FunctionScope.declareDependenciesInProject(dependencies: CodegenDependencies): Unit =
    addLambdaStatement("with(target)") {
        declareDependencies(dependencies)
    }

private fun FunctionScope.declareDependencies(dependencies: CodegenDependencies) {
    dependencies.keys.forEach { name -> addDependencyExtension(path = "dependencies", name = name) }
    if (GradmExperimentalConfiguration.kotlinMultiplatformSupport) {
        addComment("Kotlin Multiplatform Support")
        dependencies.keys.forEach { name ->
            if (name in GradmExperimentalConfiguration.kotlinMultiplatformIgnoredExtensions) return@forEach
            addDependencyExtension(name = name)
        }
    }
}

private fun FunctionScope.addDependencyExtension(path: String? = null, name: String) {
    val extensionsPath = when (path) {
        null -> "extensions"
        else -> {
            require(path.isNotBlank()) { "path must not be empty or blank" }
            require(extensionsPathRegex.matches(path)) { "path is invalid" }
            "$path.extensions"
        }
    }
    addExtensionsIfNeeds(
        extensionsPath = extensionsPath,
        name = name,
        className = ClassName(GRADM_DEPENDENCY_PACKAGE_NAME, name.capitalize()),
    )
}

private fun FunctionScope.declareVersionsInSettings(): Unit =
    addLambdaStatement("target.gradle.rootProject") {
        addLambdaStatement("allprojects") {
            declareVersions()
        }
    }

private fun FunctionScope.declareVersionsInProject(): Unit =
    addLambdaStatement(format = "with(target)", block = FunctionScope::declareVersions)

private fun FunctionScope.declareVersions(): Unit =
    addExtensionsIfNeeds(name = "versions", className = ClassName(GRADM_PACKAGE_NAME, "Versions"))

private fun FunctionScope.addExtensionsIfNeeds(
    extensionsPath: String = "extensions",
    name: String,
    className: ClassName,
): Unit =
    addStatement(format = "$extensionsPath.findByName(\"${name}\") ?: $extensionsPath.add(\"${name}\", %T)", className)

private val extensionsPathRegex = """^(\w+\.)*\w+$""".toRegex()
