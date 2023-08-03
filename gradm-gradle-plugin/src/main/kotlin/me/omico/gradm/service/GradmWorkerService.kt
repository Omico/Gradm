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
package me.omico.gradm.service

import me.omico.gradm.GradmConfiguration
import me.omico.gradm.datastore.GradmDataStore
import me.omico.gradm.info
import me.omico.gradm.internal.YamlDocument
import me.omico.gradm.internal.codegen.generateGradmGeneratedSources
import me.omico.gradm.internal.config.Repository
import me.omico.gradm.internal.config.repositories
import me.omico.gradm.internal.maven.resolveVersionsMeta
import me.omico.gradm.path.GradmProjectPaths
import me.omico.gradm.path.gradmLocalConfigurationFile
import me.omico.gradm.path.gradmMetadataFile
import me.omico.gradm.path.updatesAvailableFile
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.services.BuildServiceParameters
import org.gradle.kotlin.dsl.maven
import java.nio.file.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.exists

abstract class GradmWorkerService : GradmBuildService<BuildServiceParameters.None> {

    private lateinit var gradmConfigurationDocument: YamlDocument
    private lateinit var gradmProjectPaths: GradmProjectPaths
    private lateinit var dependencies: DependencyHandler

    private var updatesAvailableFile: Path? = null

    fun update(
        offline: Boolean,
        gradmConfigurationDocument: YamlDocument,
    ) {
        GradmConfiguration.offline = offline
        this.gradmConfigurationDocument = gradmConfigurationDocument
    }

    fun update(dependencies: DependencyHandler) {
        this.dependencies = dependencies
    }

    fun initialize(
        repositories: RepositoryHandler,
        gradmProjectPaths: GradmProjectPaths,
    ) {
        this.gradmProjectPaths = gradmProjectPaths
        GradmDataStore.initializeGradmDataStore()
        repositories.setupRepositories()
        checkUpdatesAvailable()
    }

    fun refresh() {
        GradmConfiguration.requireRefresh = true
        resolveVersionsMeta(
            dependencies = dependencies,
            gradmProjectPaths = gradmProjectPaths,
            document = gradmConfigurationDocument,
        )
        checkUpdatesAvailable()
    }

    fun generate(outputDirectory: Path) {
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
        checkUpdatesAvailable()
    }

    override fun close() {
        showAvailableUpdatesIfExists()
    }

    private fun GradmDataStore.initializeGradmDataStore() {
        load(
            localConfigurationFile = gradmProjectPaths.gradmLocalConfigurationFile,
            metadataFile = gradmMetadataFile,
        )
        updateLocalConfiguration {
            insertConfigurationPath(gradmProjectPaths.configurationFile.absolutePathString())
        }
    }

    private fun RepositoryHandler.setupRepositories() {
        gradmConfigurationDocument.repositories
            .filterNot(Repository::noUpdates)
            .forEach { repository ->
                when (repository.id) {
                    "google" -> google()
                    "mavenCentral" -> mavenCentral()
                    "gradlePluginPortal" -> gradlePluginPortal()
                    "mavenLocal" -> mavenLocal()
                    else -> maven(url = repository.url)
                }
            }
    }

    private fun checkUpdatesAvailable() {
        gradmProjectPaths.updatesAvailableFile.takeIf(Path::exists)?.let { file ->
            updatesAvailableFile = file
        }
    }

    private fun showAvailableUpdatesIfExists() {
        updatesAvailableFile?.let { file ->
            info { "Available updates found, see ${file.absolutePathString()}" }
        }
    }
}
