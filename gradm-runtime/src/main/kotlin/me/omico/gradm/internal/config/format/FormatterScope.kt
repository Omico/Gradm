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
import me.omico.gradm.internal.config.format.node.MappingNode
import me.omico.gradm.internal.config.format.node.Node
import me.omico.gradm.internal.config.format.node.ScalarNode
import me.omico.gradm.internal.config.format.node.SequenceNode
import me.omico.gradm.internal.config.gradm
import me.omico.gradm.internal.config.indent

interface FormatterScope {
    fun nested(): FormatterScope
    fun StringBuilder.appendComment(comment: String): StringBuilder
    fun StringBuilder.appendNode(key: String, node: Node): StringBuilder
}

fun FormatterScope(document: YamlDocument): FormatterScope =
    FormatterScopeImpl(
        depth = 0,
        indent = document.gradm.indent,
    )

fun FormatterScope(): FormatterScope =
    FormatterScopeImpl(
        depth = 0,
        indent = 2,
    )

data class FormatterScopeImpl(
    val depth: Int,
    val indent: Int,
) : FormatterScope {

    private val actualIndent: Int = indent * depth

    override fun nested(): FormatterScopeImpl = copy(depth = depth + 1)

    private fun StringBuilder.appendIndent(indent: Int) = append(" ".repeat(indent))

    override fun StringBuilder.appendComment(comment: String): StringBuilder = appendLine("# $comment")

    override fun StringBuilder.appendNode(key: String, node: Node): StringBuilder =
        when (node) {
            is ScalarNode -> appendScalar(key, node)
            is SequenceNode -> appendSequence(key, node)
            is MappingNode -> appendMapping(key, node)
            else -> error("Unsupported node type: ${node::class.simpleName}")
        }

    private fun StringBuilder.appendScalar(key: String, node: ScalarNode): StringBuilder =
        apply {
            appendBlockComments(node.comment.block)
            appendIndent(actualIndent).append(key).append(":").append(" ")
            when (node.style) {
                ScalarStyle.Plain -> append(node.value)
                ScalarStyle.SingleQuoted -> append("\'${node.value}\'")
                ScalarStyle.DoubleQuoted -> append("\"${node.value}\"")
            }
            appendInlineComment(node.comment.inline)
            appendLine()
        }

    private fun StringBuilder.appendSequence(key: String, node: SequenceNode): StringBuilder =
        apply {
            appendBlockComments(node.comment.block)
            appendIndent(actualIndent).append(key).append(":")
            appendInlineComment(node.comment.inline)
            appendLine()
            with(nested()) {
                node.items.forEach { node ->
                    appendIndent(actualIndent)
                    append("-").appendIndent(indent - 1)
                    when (node) {
                        is ScalarNode -> appendLine(node.value)
                        is MappingNode -> {
                            node.items.entries.forEachIndexed { index, (key, value) ->
                                with(nested()) {
                                    val stringIndex = lastIndex
                                    appendNode(key, value)
                                    if (index == 0 && value.comment.block.isEmpty())
                                        delete(stringIndex, stringIndex + actualIndent)
                                }
                            }
                        }
                        else -> TODO(
                            "This part of the code is only designed to support what Gradm needs. " +
                                "If you are interested in it, please contact me. " +
                                "I am willing to turn it into an independent library."
                        )
                    }
                }
            }
        }

    private fun StringBuilder.appendMapping(key: String, node: MappingNode): StringBuilder =
        apply {
            appendBlockComments(node.comment.block)
            appendIndent(actualIndent).append(key).append(":")
            appendInlineComment(node.comment.inline)
            appendLine()
            with(nested()) { node.items.forEach { (key, node) -> appendNode(key, node) } }
        }

    private fun StringBuilder.appendBlockComments(comments: List<String>): StringBuilder =
        apply { comments.forEach { appendComment(it) } }

    private fun StringBuilder.appendInlineComment(comment: String): StringBuilder =
        apply { if (comment.isNotEmpty()) append(" # $comment") }
}
