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
import me.omico.gradm.internal.codegen.generateDependenciesSourceFiles
import me.omico.gradm.internal.codegen.generatePluginSourceFile
import me.omico.gradm.internal.codegen.generateSelfSourceFile
import me.omico.gradm.internal.codegen.generateVersionsSourceFile
import me.omico.gradm.internal.config.Repository
import me.omico.gradm.internal.config.repositories
import me.omico.gradm.internal.maven.resolveVersionsMeta
import me.omico.gradm.path.GradmProjectPaths
import me.omico.gradm.path.updatesAvailableFile
import me.omico.gradm.service.GradmBuildService
import me.omico.gradm.utility.clearDirectory
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.services.BuildServiceParameters
import java.net.URI
import java.nio.file.Files
import java.nio.file.Path

abstract class GradmWorkerService : GradmBuildService<BuildServiceParameters.None> {

    private var updated: Boolean = false
    private var updatesAvailableFile: Path? = null

    fun initialize(
        repositories: RepositoryHandler,
        gradmConfigDocument: YamlDocument,
    ) {
        repositories.setupRepositories(gradmConfigDocument)
    }

    fun generate(
        dependencies: DependencyHandler,
        gradmProjectPaths: GradmProjectPaths,
        gradmConfigDocument: YamlDocument,
        outputDirectory: Path,
    ) {
        GradmConfiguration.requireRefresh = false
        val versionsMeta = resolveVersionsMeta(
            dependencies = dependencies,
            gradmProjectPaths = gradmProjectPaths,
            document = gradmConfigDocument,
        )
        outputDirectory.clearDirectory()
        generateDependenciesSourceFiles(outputDirectory, gradmConfigDocument, versionsMeta)
        generateVersionsSourceFile(gradmProjectPaths, outputDirectory, gradmConfigDocument)
        generatePluginSourceFile(outputDirectory, gradmConfigDocument, versionsMeta)
        generateSelfSourceFile(gradmProjectPaths, outputDirectory)
        checkUpdatesAvailable(gradmProjectPaths)
    }

    fun refresh(
        dependencies: DependencyHandler,
        gradmProjectPaths: GradmProjectPaths,
        gradmConfigDocument: YamlDocument,
    ) {
        if (updated) return
        GradmConfiguration.requireRefresh = true
        resolveVersionsMeta(
            dependencies = dependencies,
            gradmProjectPaths = gradmProjectPaths,
            document = gradmConfigDocument,
        )
        checkUpdatesAvailable(gradmProjectPaths)
    }

    override fun close() {
        updatesAvailableFile?.let { file ->
            info { "Available updates found, see ${file.toAbsolutePath()}" }
        }
    }

    private fun RepositoryHandler.setupRepositories(document: YamlDocument) {
        document.repositories
            .filterNot(Repository::noUpdates)
            .forEach { repository ->
                when (repository.id) {
                    "google" -> google()
                    "mavenCentral" -> mavenCentral()
                    "gradlePluginPortal" -> gradlePluginPortal()
                    "mavenLocal" -> mavenLocal()
                    else -> maven { url = URI.create(repository.url) }
                }
            }
    }

    private fun checkUpdatesAvailable(gradmProjectPaths: GradmProjectPaths) {
        updated = true
        updatesAvailableFile = when {
            Files.exists(gradmProjectPaths.updatesAvailableFile) -> gradmProjectPaths.updatesAvailableFile
            else -> null
        }
    }
}
