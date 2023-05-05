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

import me.omico.gradm.internal.YamlDocument
import me.omico.gradm.internal.config.Dependency
import me.omico.gradm.internal.config.Plugin
import me.omico.gradm.internal.config.dependencies
import me.omico.gradm.internal.config.format.node.mapping
import me.omico.gradm.internal.config.format.node.scalar
import me.omico.gradm.internal.config.format.node.sequence
import me.omico.gradm.internal.config.format.yaml
import me.omico.gradm.internal.config.plugins
import me.omico.gradm.internal.config.toDependency
import me.omico.gradm.path.GradmProjectPaths
import me.omico.gradm.path.updatesAvailableFile
import me.omico.gradm.path.updatesDirectory
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

internal fun YamlDocument.refreshAvailableUpdates(
    gradmProjectPaths: GradmProjectPaths,
    metadataList: List<MavenMetadata>,
) {
    val pluginsMavenUpdates = plugins.map(Plugin::toDependency).toTreeMavenUpdates(metadataList)
    val dependenciesMavenUpdates = dependencies.toTreeMavenUpdates(metadataList)
    if (pluginsMavenUpdates.isEmpty() && dependenciesMavenUpdates.isEmpty()) {
        gradmProjectPaths.updatesDirectory.deleteDirectory()
        return
    }
    val mavenUpdatesContent = yaml {
        if (pluginsMavenUpdates.isNotEmpty()) {
            mapping("plugins") {
                pluginsMavenUpdates.entries.forEach { (id, pluginUpdates) ->
                    sequence(id) {
                        pluginUpdates.forEach { (_, versions) ->
                            versions.forEach(::scalar)
                        }
                    }
                }
            }
        }
        if (dependenciesMavenUpdates.isNotEmpty()) {
            mapping("dependencies") {
                dependenciesMavenUpdates.entries.forEach { (group, artifactUpdates) ->
                    mapping(group) {
                        artifactUpdates.forEach { (artifact, versions) ->
                            sequence(artifact) { versions.forEach(::scalar) }
                        }
                    }
                }
            }
        }
    }
    with(gradmProjectPaths) {
        updatesDirectory.createDirectories()
        updatesAvailableFile.writeText(mavenUpdatesContent)
    }
}

private fun List<Dependency>.toTreeMavenUpdates(metadataList: List<MavenMetadata>): TreeMavenUpdates =
    hashMapOf<String, MutableList<ArtifactUpdates>>()
        .apply {
            this@toTreeMavenUpdates
                .map { dependency -> MavenUpdates(dependency, metadataList) }
                .filter { it.availableVersions.isNotEmpty() }
                .forEach { updates -> getOrCreate(updates.group).add(ArtifactUpdates(updates)) }
        }
        .toSortedMap()

private fun MavenUpdates(dependency: Dependency, metadataList: List<MavenMetadata>): MavenUpdates =
    MavenUpdates(
        group = dependency.group,
        artifact = dependency.artifact,
        availableVersions = dependency.availableVersions(metadataList),
    )

private fun ArtifactUpdates(mavenUpdates: MavenUpdates): ArtifactUpdates =
    ArtifactUpdates(
        artifact = mavenUpdates.artifact,
        availableVersions = mavenUpdates.availableVersions,
    )

private fun MutableTreeMavenUpdates.getOrCreate(group: String): MutableList<ArtifactUpdates> =
    getOrPut(group, ::mutableListOf)

private fun Dependency.availableVersions(metadataList: List<MavenMetadata>): List<String> = run {
    val metadata = metadataList.find { it.module == module } ?: return@run emptyList()
    if (noUpdates || version == null) return@run emptyList()
    val version = Version.parse(version)
    val newerAvailableVersions = metadata.versions
        .map(Version::parse)
        .filter { it > version }
        .sortedDescending()
        .map(Version::toString)
    when {
        newerAvailableVersions.isNotEmpty() -> newerAvailableVersions
        else -> emptyList()
    }
}
