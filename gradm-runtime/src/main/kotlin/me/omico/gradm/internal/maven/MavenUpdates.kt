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
import me.omico.gradm.internal.config.Library
import me.omico.gradm.internal.config.dependencies
import me.omico.gradm.internal.config.format.node.mapping
import me.omico.gradm.internal.config.format.node.scalar
import me.omico.gradm.internal.config.format.node.sequence
import me.omico.gradm.internal.config.format.yaml
import me.omico.gradm.internal.path.GradmPaths
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
    val treeMavenUpdates = dependencies
        .flatMap { it.libraries }
        .map { MavenUpdates(it, metadataList) }
        .filter { it.availableVersions.isNotEmpty() }
        .toTreeMavenUpdates()
    val mavenUpdatesContent = yaml {
        treeMavenUpdates.entries.forEachIndexed { index, (group, artifactUpdates) ->
            mapping(group) {
                artifactUpdates.forEach { (artifact, versions) ->
                    sequence(artifact) { versions.forEach { scalar(it) } }
                }
            }
            if (index < treeMavenUpdates.size - 1) newline()
        }
    }
    GradmPaths.Updates.rootDir.createDirectories()
    GradmPaths.Updates.available.writeText(mavenUpdatesContent)
}

internal fun List<MavenUpdates>.toTreeMavenUpdates(): TreeMavenUpdates =
    hashMapOf<String, MutableList<ArtifactUpdates>>()
        .apply { this@toTreeMavenUpdates.forEach { getOrCreate(it.group).add(ArtifactUpdates(it)) } }
        .toSortedMap()

private fun MavenUpdates(library: Library, metadataList: List<MavenMetadata>): MavenUpdates =
    MavenUpdates(
        group = library.group,
        artifact = library.artifact,
        availableVersions = library.createUpdateStatus(metadataList).availableVersions(),
    )

private fun ArtifactUpdates(mavenUpdates: MavenUpdates): ArtifactUpdates =
    ArtifactUpdates(
        artifact = mavenUpdates.artifact,
        availableVersions = mavenUpdates.availableVersions,
    )

private fun MutableTreeMavenUpdates.getOrCreate(group: String): MutableList<ArtifactUpdates> =
    this[group] ?: mutableListOf<ArtifactUpdates>().also { this[group] = it }

private fun Library.createUpdateStatus(metadataList: List<MavenMetadata>): UpdateStatus =
    metadataList.find { it.module == module }?.let { metadata ->
        val index = metadata.versions.indexOf(version)
        val newerAvailableVersions = when {
            index != -1 -> metadata.versions.subList(index + 1, metadata.versions.size)
            else -> metadata.versions
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
