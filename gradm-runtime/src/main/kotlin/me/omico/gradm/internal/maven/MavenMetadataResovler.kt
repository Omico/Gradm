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
package me.omico.gradm.internal.maven

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import me.omico.gradm.GradmConfiguration
import me.omico.gradm.VersionsMeta
import me.omico.gradm.datastore.GradmDataStore
import me.omico.gradm.datastore.maven.MavenMetadata
import me.omico.gradm.datastore.maven.module
import me.omico.gradm.debug
import me.omico.gradm.internal.YamlDocument
import me.omico.gradm.internal.config.Dependency
import me.omico.gradm.internal.config.collectAllUpdatableDependencies
import me.omico.gradm.internal.maven.bom.resolveBomVersionsMeta
import me.omico.gradm.path.GradmProjectPaths
import org.gradle.api.artifacts.dsl.DependencyHandler

fun resolveVersionsMeta(
    dependencies: DependencyHandler,
    gradmProjectPaths: GradmProjectPaths,
    document: YamlDocument,
): VersionsMeta =
    document.collectAllUpdatableDependencies()
        .let(::resolveMavenMetadata)
        .also { document.refreshAvailableUpdates(gradmProjectPaths) }
        .associate { it.module to it.latestVersion } + dependencies.resolveBomVersionsMeta(document)

private fun resolveMavenMetadata(dependencies: List<Dependency>): List<MavenMetadata> {
    GradmDataStore.updateMetadata {
        val missingDependencies = when {
            GradmConfiguration.requireRefresh -> dependencies
            else -> collectMissingMavenMetadata(dependencies)
        }
        require(!GradmConfiguration.offline || missingDependencies.isEmpty()) {
            "Cannot resolve maven-metadata.xml in offline mode."
        }
        runBlocking {
            missingDependencies
                .map { dependency -> dependency.downloadMavenMetadata() }
                .let(this@updateMetadata::insert)
        }
    }
    val modules = dependencies.map(Dependency::module)
    return GradmDataStore.metadata.maven.filter { it.module in modules }
}

private fun collectMissingMavenMetadata(dependencies: List<Dependency>): List<Dependency> {
    val localModules = GradmDataStore.metadata.maven.map(MavenMetadata::module)
    val missingDependencies = dependencies.filterNot { it.module in localModules }
    require(!GradmConfiguration.offline || missingDependencies.isEmpty()) {
        "Cannot resolve maven-metadata.xml in offline mode."
    }
    return missingDependencies
}

private suspend fun Dependency.downloadMavenMetadata(): MavenMetadata = run {
    val inputStream = withContext(Dispatchers.IO) { metadataUrl.openStream() }
    documentBuilder.parse(inputStream)
        .let { document ->
            debug { "Downloading metadata for [$module]" }
            MavenMetadata(
                uri = metadataUrl.toString(),
                repository = repository,
                groupId = document.getElementsByTagName("groupId").item(0).textContent,
                artifactId = document.getElementsByTagName("artifactId").item(0).textContent,
                latestVersion = document.getElementsByTagName("latest").item(0).textContent,
                versions = document.getElementsByTagName("versions").item(0).childNodes
                    .mapNotNull { if (it.nodeName == "version") it.textContent else null },
                lastUpdated = document.getElementsByTagName("lastUpdated").item(0).textContent.toLong(),
            )
        }
}
