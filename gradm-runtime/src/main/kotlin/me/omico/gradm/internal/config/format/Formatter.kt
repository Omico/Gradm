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
import me.omico.gradm.internal.config.gradm
import me.omico.gradm.internal.config.gradmRuleVersion
import me.omico.gradm.internal.config.gradmVersion
import me.omico.gradm.internal.config.indent
import me.omico.gradm.internal.find
import me.omico.gradm.internal.path.RootProjectPaths
import java.nio.file.Files

internal fun formatGradmConfig(document: YamlDocument) {
    if (!document.gradm.format) return
    val formatSettings = FormatSettings(document)
    Files.write(
        RootProjectPaths.gradmConfig,
        buildString {
            appendGradmBlock(document, formatSettings)
            appendVersionsBlock(document, formatSettings)
            appendRepositoriesBlock(document, formatSettings)
            appendDependenciesBlock(document, formatSettings)
        }.toByteArray(),
    )
}

private fun StringBuilder.appendGradmBlock(document: YamlDocument, formatterSettings: FormatterSettings) {
    with(formatterSettings) {
        appendLine("gradm:")
        with(nested()) {
            appendFormatLine("version", document.gradmVersion, style = FormatLineStyle.DOUBLE_QUOTES)
            appendFormatLine("rule-version", document.gradmRuleVersion)
            appendFormatLine("format", true)
            if (document.gradm.indent != 2) appendFormatLine("indent", document.gradm.indent)
        }
        appendLine()
    }
}

private fun StringBuilder.appendVersionsBlock(document: YamlDocument, formatterSettings: FormatterSettings) {
    with(formatterSettings) {
        val versions = document.find<YamlObject>("versions") ?: return
        appendLine("versions:")
        with(nested()) {
            appendFormatMap(versions)
        }
        appendLine()
    }
}

private fun StringBuilder.appendRepositoriesBlock(document: YamlDocument, formatterSettings: FormatterSettings) {
    with(formatterSettings) {
        val repositories = document.find<YamlArray>("repositories") ?: return
        appendLine("repositories:")
        with(nested()) {
            repositories
                .map(::Repository)
                .sortedBy { it.id }
                .forEach { repository ->
                    appendFormatLine(
                        key = "id",
                        value = repository.id,
                        style = FormatLineStyle.IN_SEQUENCE.copy(firstKey = true),
                    )
                    appendFormatLine(
                        key = "url",
                        value = repository.url,
                        style = FormatLineStyle.IN_SEQUENCE,
                    )
                }
        }
        appendLine()
    }
}

private fun StringBuilder.appendDependenciesBlock(document: YamlDocument, formatterSettings: FormatterSettings) {
    with(formatterSettings) {
        val dependencies = document.find<YamlArray>("dependencies") ?: return
        appendLine("dependencies:")
        with(nested()) {
            dependencies
                .map(::Dependency)
                .forEach { dependency ->
                    appendFormatLine(
                        key = "name",
                        value = dependency.name,
                        style = FormatLineStyle.IN_SEQUENCE.copy(firstKey = true),
                    )
                    appendFormatLine(
                        key = "repository",
                        value = dependency.repository,
                        style = FormatLineStyle.IN_SEQUENCE,
                    )
                    if (dependency.libraries.isEmpty()) return@forEach
                    appendFormatLine("libraries:", style = FormatLineStyle.IN_SEQUENCE)
                    with(nested()) {
                        dependency.libraries.forEach { library ->
                            with(nested()) {
                                appendFormatLine(
                                    key = "module",
                                    value = library.module,
                                    style = FormatLineStyle.IN_SEQUENCE.copy(firstKey = true),
                                )
                                appendFormatLine(
                                    key = "alias",
                                    value = library.alias,
                                    style = FormatLineStyle.IN_SEQUENCE,
                                )
                                appendFormatLine(
                                    key = "version",
                                    value = library.version,
                                    style = FormatLineStyle.IN_SEQUENCE,
                                )
                            }
                        }
                    }
                }
        }
        appendLine()
    }
}
