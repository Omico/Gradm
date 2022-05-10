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

import me.omico.gradm.internal.config.format.node.MappingNodeScope
import me.omico.gradm.internal.config.format.node.Node

interface YamlScope : MappingNodeScope {
    fun newline()
    fun comment(comment: String)
}

class YamlBuilder(
    private val formatterScope: FormatterScope = FormatterScope(),
) : YamlScope, FormatterScope by formatterScope, Builder<String> {

    private val stringBuilder = StringBuilder()

    override fun newline() {
        stringBuilder.appendLine()
    }

    override fun comment(comment: String) {
        stringBuilder.appendComment(comment)
    }

    override fun put(key: String, node: Node) {
        stringBuilder.appendNode(key, node)
    }

    override fun build(): String = stringBuilder.toString()
}

fun YamlBuilder.comments(vararg comments: String) = comments.forEach { comment(it) }
fun YamlBuilder.comments(comments: List<String>) = comments.forEach { comment(it) }

fun yaml(
    formatterScope: FormatterScope = FormatterScope(),
    block: YamlBuilder.() -> Unit,
): String = YamlBuilder(formatterScope).apply(block).build()
