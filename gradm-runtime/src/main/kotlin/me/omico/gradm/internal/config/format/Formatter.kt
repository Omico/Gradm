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
package me.omico.gradm.internal.config.format

import me.omico.gradm.GradmFormatConfiguration
import me.omico.gradm.internal.YamlDocument
import me.omico.gradm.internal.YamlObject
import me.omico.gradm.internal.asYamlDocument
import me.omico.gradm.internal.config.buildInRepositories
import me.omico.gradm.internal.config.format.node.MappingNodeScope
import me.omico.gradm.internal.config.format.node.mapping
import me.omico.gradm.internal.config.format.node.scalar
import me.omico.gradm.internal.config.repositories
import me.omico.gradm.internal.config.versionVariableRegex
import me.omico.gradm.internal.find
import me.omico.gradm.internal.require
import java.nio.file.Path
import kotlin.io.path.writeText

fun formatGradmConfig(configFile: Path) {
    configFile.writeText(configFile.createFormattedGradmConfigContent())
}

fun Path.createFormattedGradmConfigContent(): String =
    yaml(formatterScope = FormatterScope(indent = GradmFormatConfiguration.indent)) {
        val document = asYamlDocument()
        versionsMapping(document)
        repositoriesSequence(document)
        pluginsMapping(document)
        dependenciesMapping(document)
    }

fun YamlScope.versionsMapping(document: YamlDocument) {
    val versions = document.find<YamlObject>("versions") ?: return
    mapping("versions") {
        recursiveVersionsMapping(versions)
    }
    requireNewline = true
}

fun MappingNodeScope.recursiveVersionsMapping(versions: Map<*, *>): Unit =
    versions.toSortedMap(comparator = compareBy(Any?::toString)).forEach { (key, value) ->
        val formattedKey = key.toString().replace("-", ".")
        when (value) {
            is Map<*, *> -> mapping(formattedKey) { recursiveVersionsMapping(value) }
            else -> scalar(formattedKey, value.toString(), ScalarStyle.DoubleQuoted)
        }
    }

fun YamlScope.repositoriesSequence(document: YamlDocument) {
    val repositories = document.repositories
    if (repositories.isEmpty()) return
    newlineIfNeeded()
    mapping("repositories") {
        repositories
            .filter { !it.noUpdates || it.id == "mavenLocal" }
            .forEach { repository ->
                mapping(repository.id) repository@{
                    if (buildInRepositories.any { it.id == repository.id }) return@repository
                    scalar("url", repository.url, ScalarStyle.DoubleQuoted)
                }
            }
        repositories.find { it.id == "noUpdates" }?.id?.let(::mapping)
        repositories
            .filterNot { it.id == "noUpdates" }
            .filter { it.noUpdates && it.id != "mavenLocal" }
            .sortedBy { it.id }
            .forEach { repository ->
                mapping(repository.id) {
                    if (repository.url.isNotBlank()) {
                        scalar("url", repository.url, ScalarStyle.DoubleQuoted)
                    }
                    scalar("noUpdates", true)
                }
            }
    }
    requireNewline = true
}

@Suppress("UNCHECKED_CAST")
fun YamlScope.pluginsMapping(document: YamlDocument) {
    val plugins = document.find<YamlObject>("plugins") ?: return
    newlineIfNeeded()
    mapping("plugins") {
        plugins.toSortedMap().forEach { (repository, plugins) ->
            mapping(repository) {
                plugins as YamlObject
                plugins.toSortedMap().forEach { (id, version) ->
                    version as String
                    scalar(id, version, style = decideVersionStyle(version))
                }
            }
        }
    }
    requireNewline = true
}

@Suppress("UNCHECKED_CAST")
fun YamlScope.dependenciesMapping(document: YamlDocument) {
    val dependencies = document.find<YamlObject>("dependencies") ?: return
    newlineIfNeeded()
    mapping("dependencies") {
        dependencies.toSortedMap().forEach { (repository, groups) ->
            mapping(repository) {
                (groups as YamlObject).toSortedMap().forEach { (group, artifacts) ->
                    mapping(group) {
                        (artifacts as YamlObject).toSortedMap().forEach { (artifact, attributes) ->
                            attributes as YamlObject
                            mapping(artifact) {
                                scalar("alias", attributes.require("alias"))
                                attributes.find<String>("version")?.let { version ->
                                    scalar("version", version, style = decideVersionStyle(version))
                                }
                                if (attributes.find("noSpecificVersion", false)) {
                                    scalar("noSpecificVersion", true)
                                }
                                if (attributes.find("bom", false)) {
                                    scalar("bom", true)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun decideVersionStyle(version: String): ScalarStyle = when {
    versionVariableRegex.matches(version) -> ScalarStyle.Plain
    else -> ScalarStyle.DoubleQuoted
}

private var requireNewline: Boolean = false

private fun YamlScope.newlineIfNeeded() {
    if (!requireNewline) return
    newline()
    requireNewline = false
}
