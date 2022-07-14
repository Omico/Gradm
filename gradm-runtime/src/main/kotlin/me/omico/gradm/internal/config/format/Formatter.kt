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
package me.omico.gradm.internal.config.format

import me.omico.gradm.GradmConfigs
import me.omico.gradm.internal.YamlDocument
import me.omico.gradm.internal.YamlObject
import me.omico.gradm.internal.config.fixedUrl
import me.omico.gradm.internal.config.format.node.MappingNodeScope
import me.omico.gradm.internal.config.format.node.mapping
import me.omico.gradm.internal.config.format.node.scalar
import me.omico.gradm.internal.config.versionVariableRegex
import me.omico.gradm.internal.find
import me.omico.gradm.internal.path.RootProjectPaths
import me.omico.gradm.internal.require
import kotlin.io.path.writeText

fun formatGradmConfig(document: YamlDocument) {
    if (!GradmConfigs.format) return
    RootProjectPaths.gradmConfig.writeText(createFormattedGradmConfigContent(document))
}

fun createFormattedGradmConfigContent(document: YamlDocument): String =
    yaml(formatterScope = FormatterScope(indent = GradmConfigs.indent)) {
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
    newline()
}

fun MappingNodeScope.recursiveVersionsMapping(versions: Map<*, *>): Unit =
    versions.toSortedMap(comparator = compareBy(Any?::toString)).forEach { (key, value) ->
        val formattedKey = key.toString().replace("-", ".")
        when (value) {
            is Map<*, *> -> mapping(formattedKey) { recursiveVersionsMapping(value) }
            else -> scalar(formattedKey, value.toString(), ScalarStyle.DoubleQuoted)
        }
    }

@Suppress("UNCHECKED_CAST")
fun YamlScope.repositoriesSequence(document: YamlDocument) {
    val repositories = document.find<YamlObject>("repositories") ?: return
    mapping("repositories") {
        repositories.toSortedMap().forEach { (id, repository) ->
            repository as YamlObject
            mapping(id) {
                scalar("url", repository.require<String>("url").fixedUrl())
            }
        }
    }
    newline()
}

@Suppress("UNCHECKED_CAST")
fun YamlScope.pluginsMapping(document: YamlDocument) {
    val plugins = document.find<YamlObject>("plugins") ?: return
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
    newline()
}

@Suppress("UNCHECKED_CAST")
fun YamlScope.dependenciesMapping(document: YamlDocument) {
    val dependencies = document.find<YamlObject>("dependencies") ?: return
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
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun decideVersionStyle(version: String): ScalarStyle =
    when {
        versionVariableRegex.matches(version) -> ScalarStyle.Plain
        else -> ScalarStyle.DoubleQuoted
    }
