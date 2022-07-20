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
package me.omico.gradm.internal.maven

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import me.omico.gradm.GradmConfigs
import me.omico.gradm.VersionsMeta
import me.omico.gradm.asVersionsMetaHash
import me.omico.gradm.debug
import me.omico.gradm.internal.YamlDocument
import me.omico.gradm.internal.config.Dependency
import me.omico.gradm.internal.config.Plugin
import me.omico.gradm.internal.config.dependencies
import me.omico.gradm.internal.config.metadataLocalPath
import me.omico.gradm.internal.config.plugins
import me.omico.gradm.internal.config.toDependency
import me.omico.gradm.internal.path.GradmPaths
import me.omico.gradm.internal.sha1
import me.omico.gradm.localVersionsMeta
import me.omico.gradm.utility.deleteDirectory
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.deleteExisting
import kotlin.io.path.readBytes
import kotlin.io.path.writeBytes
import kotlin.io.path.writeText

fun refreshVersionsMeta(document: YamlDocument): VersionsMeta =
    hashMapOf<String, String>()
        .apply { document.downloadAllMetadata() }
        .apply { putAll(document.localVersionsMeta) }
        .also { document.refreshAvailableUpdates() }

internal fun YamlDocument.collectAllMetadataFiles(): List<Path> =
    ArrayList<Dependency>()
        .apply { addAll(plugins.map(Plugin::toDependency)) }
        .apply { addAll(dependencies) }
        .filterNot { it.noUpdates || it.noSpecificVersion }
        .runCatching { map { it.metadataLocalPath(GradmPaths.Metadata.rootDir) } }
        .onFailure { it.printStackTrace() }
        .getOrDefault(emptyList())

internal fun YamlDocument.collectAllMetadata(): List<MavenMetadata> =
    collectAllMetadataFiles().map(::MavenMetadata)

private fun YamlDocument.downloadAllMetadata() =
    runBlocking {
        if (GradmConfigs.offline) return@runBlocking
        if (!requireUpdateMetadata) {
            debug { "Use cached metadata, skipping download." }
            return@runBlocking
        }
        debug { "Downloading plugins metadata" }
        plugins.forEach { plugin -> plugin.toDependency().downloadMetadata() }
        debug { "Downloading dependencies metadata" }
        dependencies.forEach { dependency -> dependency.downloadMetadata() }
        refreshHash()
    }

private suspend fun Dependency.downloadMetadata() {
    if (noUpdates) {
        debug { "Skipping [$module] because noUpdates is set to true" }
        return
    }
    if (noSpecificVersion) {
        debug { "Skipping [$module] because noSpecificVersion is set to true" }
        metadataLocalPath(GradmPaths.Metadata.rootDir).parent.deleteDirectory()
        return
    }
    debug { "Downloading metadata for [$module]" }
    val bytes = withContext(Dispatchers.IO) { metadataUrl.readBytes() }
    val metadataPath = metadataLocalPath(GradmPaths.Metadata.rootDir)
    metadataPath.parent.createDirectories()
    metadataPath.writeBytes(bytes)
}

private val YamlDocument.calculateMetadataHash
    get() = runCatching { collectAllMetadataFiles().map(Path::readBytes).sha1() }.getOrNull()

private fun YamlDocument.refreshHash() =
    calculateMetadataHash
        ?.let { GradmPaths.Metadata.versionsMetaHash.writeText(it) }
        ?: GradmPaths.Metadata.versionsMetaHash.deleteExisting()

private val YamlDocument.requireUpdateMetadata: Boolean
    get() = GradmConfigs.updateDependencies ||
        runCatching { GradmPaths.Metadata.versionsMetaHash.asVersionsMetaHash() != calculateMetadataHash }
            .getOrDefault(GradmConfigs.updateDependencies)
