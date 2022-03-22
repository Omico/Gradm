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

import me.omico.gradm.internal.YamlDocument
import me.omico.gradm.internal.config.gradm
import me.omico.gradm.internal.config.indent

data class FormatterSettings(
    val depth: Int,
    val indent: Int,
) {

    private val actualIndent: Int = indent * depth

    fun nested(): FormatterSettings = copy(depth = depth + 1)

    fun StringBuilder.appendFormatLine(value: String, style: FormatLineStyle = FormatLineStyle.DEFAULT) {
        appendIndent(actualIndent)
        with(style) {
            when {
                inSequence -> when {
                    firstKey -> append("-" + " ".repeat(indent - 1))
                    else -> appendIndent(indent)
                }
                else -> return@with
            }
        }
        appendLine(value)
    }

    fun StringBuilder.appendFormatLine(key: String, value: Any?, style: FormatLineStyle = FormatLineStyle.DEFAULT) {
        value ?: return
        appendFormatLine("$key: ${if (style.doubleQuotes) "\"$value\"" else value}", style)
    }

    fun StringBuilder.appendFormatMap(map: Map<*, *>): Unit =
        map.sortByKey()
            .forEach { (key, value) ->
                when (value) {
                    is Map<*, *> -> {
                        appendFormatLine("$key:")
                        with(nested()) { appendFormatMap(value) }
                    }
                    else -> appendFormatLine(key.toString(), value.toString(), style = FormatLineStyle.DOUBLE_QUOTES)
                }
            }

    private fun StringBuilder.appendIndent(indent: Int) {
        append(" ".repeat(indent))
    }

    private fun Map<*, *>.sortByKey(): Map<*, *> = map { (key, value) ->
        when (value) {
            is Map<*, *> -> key to value.sortByKey()
            else -> key to value
        }
    }.sortedBy { it.first.toString() }.toMap()
}

@Suppress("FunctionName")
fun FormatSettings(document: YamlDocument): FormatterSettings =
    FormatterSettings(
        depth = 0,
        indent = document.gradm.indent,
    )
