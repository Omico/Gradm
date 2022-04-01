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
import me.omico.gradm.VersionsMeta
import me.omico.gradm.internal.YamlDocument
import me.omico.gradm.internal.config.dependencies
import me.omico.gradm.internal.path.GradmPaths

internal fun generateDependenciesSourceFiles(document: YamlDocument, versionsMeta: VersionsMeta) {
    val codegenDependencies = document.createCodegenDependencies(versionsMeta)
    codegenDependencies.forEach { dependency ->
        dependency.toFileSpec().writeTo(GradmPaths.GeneratedDependenciesProject.sourceDir)
    }
    codegenDependencies.toDslFileSpec().writeTo(GradmPaths.GeneratedDependenciesProject.sourceDir)
}

private typealias CodegenDependencies = List<CodegenDependency>

private fun YamlDocument.createCodegenDependencies(versionsMeta: VersionsMeta): CodegenDependencies =
    dependencies.sortedBy { it.name }.map(versionsMeta::toCodegenDependency)

private fun CodegenDependencies.toDslFileSpec(): FileSpec =
    FileSpec.builder("", "Dependencies")
        .addSuppressWarningTypes()
        .addGradmComment()
        .apply {
            forEach { dependency ->
                addDslProperty(
                    propertyName = dependency.name,
                    receivers = arrayOf(
                        ClassName("org.gradle.api.artifacts.dsl", "DependencyHandler"),
                        ClassName("org.jetbrains.kotlin.gradle.plugin", "KotlinDependencyHandler"),
                    )
                )
            }
        }
        .build()
