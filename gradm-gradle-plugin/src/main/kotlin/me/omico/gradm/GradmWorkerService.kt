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

import me.omico.gradm.datastore.GradmDataStore
import me.omico.gradm.internal.YamlDocument
import me.omico.gradm.internal.codegen.generateGradmGeneratedSources
import me.omico.gradm.internal.config.Repository
import me.omico.gradm.internal.config.repositories
import me.omico.gradm.internal.maven.resolveVersionsMeta
import me.omico.gradm.path.GradmProjectPaths
import me.omico.gradm.path.gradmLocalConfigurationFile
import me.omico.gradm.path.gradmMetadataFile
import me.omico.gradm.path.updatesAvailableFile
import me.omico.gradm.service.GradmBuildService
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.services.BuildServiceParameters
import java.net.URI
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.absolutePathString

abstract class GradmWorkerService : GradmBuildService<BuildServiceParameters.None> {

    private var updated: Boolean = false
    private var updatesAvailableFile: Path? = null

    fun initialize(
        repositories: RepositoryHandler,
        gradmProjectPaths: GradmProjectPaths,
        gradmConfigurationDocument: YamlDocument,
    ) {
        initializeGradmDataStore(gradmProjectPaths)
        repositories.setupRepositories(gradmConfigurationDocument)
    }

    fun generate(
        dependencies: DependencyHandler,
        gradmProjectPaths: GradmProjectPaths,
        gradmConfigurationDocument: YamlDocument,
        outputDirectory: Path,
    ) {
        GradmConfiguration.requireRefresh = false
        val versionsMeta = resolveVersionsMeta(
            dependencies = dependencies,
            gradmProjectPaths = gradmProjectPaths,
            document = gradmConfigurationDocument,
        )
        generateGradmGeneratedSources(
            gradmProjectPaths = gradmProjectPaths,
            gradmConfigurationDocument = gradmConfigurationDocument,
            versionsMeta = versionsMeta,
            generatedSourcesDirectory = outputDirectory,
        )
        checkUpdatesAvailable(gradmProjectPaths)
    }

    fun refresh(
        dependencies: DependencyHandler,
        gradmProjectPaths: GradmProjectPaths,
        gradmConfigurationDocument: YamlDocument,
    ) {
        if (updated) return
        GradmConfiguration.requireRefresh = true
        resolveVersionsMeta(
            dependencies = dependencies,
            gradmProjectPaths = gradmProjectPaths,
            document = gradmConfigurationDocument,
        )
        checkUpdatesAvailable(gradmProjectPaths)
    }

    override fun close() {
        updatesAvailableFile?.let { file ->
            info { "Available updates found, see ${file.toAbsolutePath()}" }
        }
    }

    private var isGradmDataStoreInitialized = false
    private fun initializeGradmDataStore(
        gradmProjectPaths: GradmProjectPaths,
    ) {
        if (isGradmDataStoreInitialized) return
        GradmDataStore.load(
            localConfigurationFile = gradmProjectPaths.gradmLocalConfigurationFile,
            metadataFile = gradmMetadataFile,
        )
        GradmDataStore.updateLocalConfiguration {
            insertConfigurationPath(gradmProjectPaths.configurationFile.absolutePathString())
        }
        isGradmDataStoreInitialized = true
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
