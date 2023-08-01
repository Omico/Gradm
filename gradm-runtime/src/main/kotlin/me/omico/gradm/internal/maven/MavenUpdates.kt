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

import me.omico.gradm.datastore.GradmDataStore
import me.omico.gradm.datastore.maven.module
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

internal typealias TreeMavenUpdates = Map<String, List<MavenArtifactUpdates>>
internal typealias MutableTreeMavenUpdates = MutableMap<String, MutableList<MavenArtifactUpdates>>

internal data class MavenArtifactUpdates(
    val artifact: String,
    val availableVersions: List<String>,
)

internal fun YamlDocument.refreshAvailableUpdates(gradmProjectPaths: GradmProjectPaths) {
    val pluginsMavenUpdates = plugins.map(Plugin::toDependency).toTreeMavenUpdates()
    val dependenciesMavenUpdates = dependencies.toTreeMavenUpdates()
    if (pluginsMavenUpdates.isEmpty() && dependenciesMavenUpdates.isEmpty()) {
        gradmProjectPaths.updatesDirectory.deleteDirectory()
        return
    }
    val mavenUpdatesContent = yaml {
        if (pluginsMavenUpdates.isNotEmpty()) {
            mapping("plugins") {
                pluginsMavenUpdates.entries.forEach { (id, pluginUpdates) ->
                    sequence(id) {
                        pluginUpdates.forEach { (_, versions) -> versions.forEach(::scalar) }
                    }
                }
            }
        }
        if (dependenciesMavenUpdates.isNotEmpty()) {
            if (pluginsMavenUpdates.isNotEmpty()) newline()
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

private fun List<Dependency>.toTreeMavenUpdates(): TreeMavenUpdates =
    hashMapOf<String, MutableList<MavenArtifactUpdates>>()
        .apply {
            this@toTreeMavenUpdates.forEach { dependency ->
                val updates = MavenArtifactUpdates(dependency)
                if (updates.availableVersions.isEmpty()) return@forEach
                getOrCreate(dependency.group).add(updates)
            }
        }
        .toSortedMap()

private fun MavenArtifactUpdates(dependency: Dependency): MavenArtifactUpdates =
    MavenArtifactUpdates(
        artifact = dependency.artifact,
        availableVersions = dependency.availableVersions,
    )

private fun MutableTreeMavenUpdates.getOrCreate(group: String): MutableList<MavenArtifactUpdates> =
    getOrPut(group, ::mutableListOf)

private val Dependency.availableVersions: List<String>
    get() = run {
        val metadata = GradmDataStore.metadata.maven.find { it.module == module } ?: return@run emptyList()
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
