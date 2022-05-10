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

import me.omico.gradm.internal.YamlArray
import me.omico.gradm.internal.YamlDocument
import me.omico.gradm.internal.YamlObject
import me.omico.gradm.internal.config.Dependency
import me.omico.gradm.internal.config.Repository
import me.omico.gradm.internal.config.format
import me.omico.gradm.internal.config.format.node.MappingNodeScope
import me.omico.gradm.internal.config.format.node.mapping
import me.omico.gradm.internal.config.format.node.scalar
import me.omico.gradm.internal.config.format.node.sequence
import me.omico.gradm.internal.config.gradm
import me.omico.gradm.internal.config.gradmRuleVersion
import me.omico.gradm.internal.config.indent
import me.omico.gradm.internal.find
import me.omico.gradm.internal.path.RootProjectPaths
import kotlin.io.path.writeText

fun formatGradmConfig(document: YamlDocument) {
    if (!document.gradm.format) return
    RootProjectPaths.gradmConfig.writeText(createFormattedGradmConfigContent(document))
}

fun createFormattedGradmConfigContent(document: YamlDocument): String =
    yaml(formatterScope = FormatterScope(document)) {
        mapping("gradm") {
            scalar("rule-version", document.gradmRuleVersion)
            scalar("format", true)
            if (document.gradm.indent != 2) scalar("indent", document.gradm.indent)
        }
        newline()
        versionsMapping(document)
        newline()
        repositoriesSequence(document)
        newline()
        dependenciesMapping(document)
    }

fun YamlScope.versionsMapping(document: YamlDocument) {
    val versions = document.find<YamlObject>("versions") ?: return
    mapping("versions") {
        recursiveVersionsMapping(versions)
    }
}

fun MappingNodeScope.recursiveVersionsMapping(versions: Map<*, *>): Unit =
    versions.toSortedMap(comparator = compareBy(Any?::toString)).forEach { (key, value) ->
        when (value) {
            is Map<*, *> -> mapping(key.toString()) { recursiveVersionsMapping(value) }
            else -> scalar(key.toString(), value.toString(), style = ScalarStyle.DoubleQuoted)
        }
    }

fun YamlScope.repositoriesSequence(document: YamlDocument) {
    val repositories = document.find<YamlArray>("repositories") ?: return
    sequence("repositories") {
        repositories.map(::Repository).sortedBy { it.id }.forEach { repository ->
            mapping {
                scalar("id", repository.id)
                scalar("url", repository.url)
            }
        }
    }
}

fun YamlScope.dependenciesMapping(document: YamlDocument) {
    val dependencies = document.find<YamlArray>("dependencies") ?: return
    sequence("dependencies") {
        dependencies.map(::Dependency).forEach { dependency ->
            if (dependency.libraries.isNotEmpty()) mapping {
                scalar("name", dependency.name)
                scalar("repository", dependency.repository)
                sequence("libraries") {
                    dependency.libraries.forEach { library ->
                        mapping {
                            scalar("module", library.module)
                            library.alias?.let { scalar("alias", it) }
                            library.version?.let { scalar("version", it) }
                        }
                    }
                }
            }
        }
    }
}
