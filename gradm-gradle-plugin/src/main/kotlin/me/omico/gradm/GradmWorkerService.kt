/*
 * Copyright 2023 Omico
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
package me.omico.gradm

import me.omico.gradm.internal.YamlDocument
import me.omico.gradm.internal.asYamlDocument
import me.omico.gradm.internal.codegen.generateDependenciesSourceFiles
import me.omico.gradm.internal.codegen.generatePluginSourceFile
import me.omico.gradm.internal.codegen.generateSelfSourceFile
import me.omico.gradm.internal.codegen.generateVersionsSourceFile
import me.omico.gradm.internal.config.format.formatGradmConfig
import me.omico.gradm.internal.maven.resolveVersionsMeta
import me.omico.gradm.path.GradmProjectPaths
import me.omico.gradm.path.gradmProjectPaths
import me.omico.gradm.path.updatesAvailableFile
import me.omico.gradm.service.GradmBuildService
import me.omico.gradm.utility.clearDirectory
import org.gradle.api.Project
import org.gradle.api.services.BuildServiceParameters
import java.nio.file.Files
import java.nio.file.Path

abstract class GradmWorkerService : GradmBuildService<BuildServiceParameters.None> {

    private var updatesAvailableFile: Path? = null

    fun generate(project: Project, gradmConfigFile: Path, outputDirectory: Path) {
        formatGradmConfig(gradmConfigFile)
        val document = gradmConfigFile.asYamlDocument()
        val gradmProjectPaths = project.gradmProjectPaths
        val versionsMeta = resolveVersionsMeta(project, gradmProjectPaths, document)
        outputDirectory.clearDirectory()
        generateDependenciesSourceFiles(outputDirectory, document, versionsMeta)
        generateVersionsSourceFile(gradmProjectPaths, outputDirectory, document)
        generatePluginSourceFile(outputDirectory, document, versionsMeta)
        generateSelfSourceFile(gradmProjectPaths, outputDirectory)
    }

    fun refresh(
        project: Project,
        gradmConfigFile: Path,
    ) {
        GradmConfiguration.requireRefresh = true
        formatGradmConfig(gradmConfigFile)
        resolveVersionsMeta(
            project = project,
            gradmProjectPaths = project.gradmProjectPaths,
            document = gradmConfigFile.asYamlDocument(),
        )
    }

    override fun close() {
        updatesAvailableFile?.let { file ->
            info { "Available updates found, see ${file.toAbsolutePath()}" }
        }
    }

    private fun resolveVersionsMeta(
        project: Project,
        gradmProjectPaths: GradmProjectPaths,
        document: YamlDocument,
    ): VersionsMeta =
        project.resolveVersionsMeta(gradmProjectPaths, document)
            .also {
                updatesAvailableFile = when {
                    Files.exists(gradmProjectPaths.updatesAvailableFile) -> gradmProjectPaths.updatesAvailableFile
                    else -> null
                }
            }
}
