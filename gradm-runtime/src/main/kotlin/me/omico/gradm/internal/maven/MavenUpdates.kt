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

import me.omico.gradm.internal.YamlDocument
import me.omico.gradm.internal.config.Dependency
import me.omico.gradm.internal.config.dependencies
import me.omico.gradm.internal.config.format.node.mapping
import me.omico.gradm.internal.config.format.node.scalar
import me.omico.gradm.internal.config.format.node.sequence
import me.omico.gradm.internal.config.format.yaml
import me.omico.gradm.internal.config.plugins
import me.omico.gradm.internal.config.toDependency
import me.omico.gradm.internal.path.GradmPaths
import me.omico.gradm.utility.deleteDirectory
import kotlin.io.path.createDirectories
import kotlin.io.path.writeText

internal data class MavenUpdates(
    val group: String,
    val artifact: String,
    val availableVersions: List<String>,
)

internal typealias TreeMavenUpdates = Map<String, List<ArtifactUpdates>>
internal typealias MutableTreeMavenUpdates = MutableMap<String, MutableList<ArtifactUpdates>>

internal data class ArtifactUpdates(
    val artifact: String,
    val availableVersions: List<String>,
)

internal sealed interface UpdateStatus {

    object UpToDate : UpdateStatus

    data class UpdatesAvailable(
        val versions: List<String>,
    ) : UpdateStatus

    object UpdateNotFound : UpdateStatus
}

internal fun YamlDocument.storeAvailableUpdates(metadataList: List<MavenMetadata>) {
    val pluginsMavenUpdates = plugins
        .map { MavenUpdates(it.toDependency(), metadataList) }
        .filter { it.availableVersions.isNotEmpty() }
        .toTreeMavenUpdates()
    val dependenciesMavenUpdates = dependencies
        .map { MavenUpdates(it, metadataList) }
        .filter { it.availableVersions.isNotEmpty() }
        .toTreeMavenUpdates()
    if (pluginsMavenUpdates.isEmpty() && dependenciesMavenUpdates.isEmpty()) {
        GradmPaths.Updates.rootDir.deleteDirectory()
        return
    }
    val mavenUpdatesContent = yaml {
        if (pluginsMavenUpdates.isNotEmpty()) mapping("plugins") {
            pluginsMavenUpdates.entries.forEachIndexed { index, (id, pluginUpdates) ->
                sequence(id) {
                    pluginUpdates.forEach { (_, versions) ->
                        versions.forEach { scalar(it) }
                    }
                }
                if (index < dependenciesMavenUpdates.size - 1) newline()
            }
        }
        if (dependenciesMavenUpdates.isNotEmpty()) mapping("dependencies") {
            dependenciesMavenUpdates.entries.forEachIndexed { index, (group, artifactUpdates) ->
                mapping(group) {
                    artifactUpdates.forEach { (artifact, versions) ->
                        sequence(artifact) { versions.forEach { scalar(it) } }
                    }
                }
                if (index < dependenciesMavenUpdates.size - 1) newline()
            }
        }
    }
    GradmPaths.Updates.rootDir.createDirectories()
    GradmPaths.Updates.available.writeText(mavenUpdatesContent)
}

internal fun List<MavenUpdates>.toTreeMavenUpdates(): TreeMavenUpdates =
    hashMapOf<String, MutableList<ArtifactUpdates>>()
        .apply { this@toTreeMavenUpdates.forEach { getOrCreate(it.group).add(ArtifactUpdates(it)) } }
        .toSortedMap()

private fun MavenUpdates(dependency: Dependency, metadataList: List<MavenMetadata>): MavenUpdates =
    MavenUpdates(
        group = dependency.group,
        artifact = dependency.artifact,
        availableVersions = dependency.createUpdateStatus(metadataList).availableVersions(),
    )

private fun ArtifactUpdates(mavenUpdates: MavenUpdates): ArtifactUpdates =
    ArtifactUpdates(
        artifact = mavenUpdates.artifact,
        availableVersions = mavenUpdates.availableVersions,
    )

private fun MutableTreeMavenUpdates.getOrCreate(group: String): MutableList<ArtifactUpdates> =
    getOrPut(group) { mutableListOf() }

private fun Dependency.createUpdateStatus(metadataList: List<MavenMetadata>): UpdateStatus =
    metadataList.find { it.module == module }?.let { metadata ->
        if (noUpdates) return@let UpdateStatus.UpToDate
        val newerAvailableVersions = when (val index = metadata.versions.indexOf(version)) {
            -1 -> metadata.versions
            0 -> emptyList()
            else -> metadata.versions.subList(index + 1, metadata.versions.size)
        }
        when {
            newerAvailableVersions.isNotEmpty() -> UpdateStatus.UpdatesAvailable(newerAvailableVersions.asReversed())
            else -> UpdateStatus.UpToDate
        }
    } ?: UpdateStatus.UpdateNotFound

private fun UpdateStatus.availableVersions(): List<String> =
    when (this) {
        is UpdateStatus.UpdatesAvailable -> versions
        else -> emptyList()
    }
