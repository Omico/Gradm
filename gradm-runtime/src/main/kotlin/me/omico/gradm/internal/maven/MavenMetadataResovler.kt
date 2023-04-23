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
import me.omico.gradm.debug
import me.omico.gradm.internal.YamlDocument
import me.omico.gradm.internal.config.Dependency
import me.omico.gradm.internal.config.collectAllDependencies
import me.omico.gradm.internal.config.localMetadataFile
import me.omico.gradm.internal.maven.bom.resolveBomVersionsMeta
import me.omico.gradm.path.GradmProjectPaths
import me.omico.gradm.path.metadataDirectory
import org.gradle.api.artifacts.dsl.DependencyHandler
import java.nio.file.Path
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.absolute
import kotlin.io.path.createDirectories
import kotlin.io.path.isRegularFile
import kotlin.io.path.name
import kotlin.io.path.walk
import kotlin.io.path.writeBytes

fun resolveVersionsMeta(
    dependencies: DependencyHandler,
    gradmProjectPaths: GradmProjectPaths,
    document: YamlDocument,
): VersionsMeta = run {
    val metadataDirectory = gradmProjectPaths.metadataDirectory
    metadataDirectory.createDirectories()
    document.collectAllRequiredMetadata(metadataDirectory)
        .also { resolveMavenMetadataFiles(it, metadataDirectory) }
        .map { MavenMetadata(it.value) }
        .also { document.refreshAvailableUpdates(gradmProjectPaths, it) }
        .associate { it.module to it.latestVersion } + dependencies.resolveBomVersionsMeta(document)
}

private fun YamlDocument.collectAllRequiredMetadata(metadataFolder: Path): Map<Dependency, Path> =
    collectAllDependencies()
        .filterNot(Dependency::noUpdates)
        .filterNot(Dependency::noSpecificVersion)
        .associateWith { it.localMetadataFile(metadataFolder).absolute() }

private fun resolveMavenMetadataFiles(
    requiredMavenMetadataMap: Map<Dependency, Path>,
    metadataDirectory: Path,
) {
    val localMetadataFiles = collectLocalMetadataFiles(metadataDirectory)
    runBlocking {
        val mavenMetadataMap = when {
            GradmConfiguration.requireRefresh -> requiredMavenMetadataMap
            else -> requiredMavenMetadataMap.filterNot { localMetadataFiles.contains(it.value) }
        }
        require(!GradmConfiguration.offline || mavenMetadataMap.isEmpty()) {
            "Cannot resolve maven-metadata.xml in offline mode."
        }
        mavenMetadataMap.forEach { (dependency, metadataFile) -> dependency.downloadMetadata(metadataFile) }
    }
}

private suspend fun Dependency.downloadMetadata(metadataFile: Path) {
    debug { "Downloading metadata for [$module]" }
    val bytes = withContext(Dispatchers.IO) { metadataUrl.readBytes() }
    with(metadataFile) {
        parent.createDirectories()
        writeBytes(bytes)
    }
}

@OptIn(ExperimentalPathApi::class)
private fun collectLocalMetadataFiles(metadataDirectory: Path): List<Path> =
    metadataDirectory.walk()
        .filter(Path::isRegularFile)
        .filter { it.name == "maven-metadata.xml" }
        .map(Path::absolute)
        .toList()
