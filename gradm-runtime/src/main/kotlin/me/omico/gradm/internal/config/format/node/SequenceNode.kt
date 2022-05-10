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
package me.omico.gradm.internal.config.format.node

import me.omico.gradm.internal.config.format.Builder
import me.omico.gradm.internal.config.format.Comment
import me.omico.gradm.internal.config.format.ScalarStyle

typealias YamlSequence = List<Node>

data class SequenceNode(
    override val items: YamlSequence,
    override val comment: Comment = Comment(),
) : CollectionNode<YamlSequence>, YamlSequence by items

interface SequenceNodeScope {
    fun add(node: Node)
}

fun <T : Any> SequenceNodeScope.scalar(
    value: T,
    scalarStyle: ScalarStyle = ScalarStyle.Plain,
    comment: Comment = Comment(),
) = add(ScalarNode(value, scalarStyle, comment))

fun SequenceNodeScope.mapping(
    values: YamlMapping,
    comment: Comment = Comment(),
) = add(MappingNode(values, comment))

fun SequenceNodeScope.mapping(
    comment: Comment = Comment(),
    block: YamlMappingBuilder.() -> Unit,
) = mapping(mapping(block), comment)

class YamlSequenceBuilder : SequenceNodeScope, Builder<YamlSequence> {

    private val sequence = mutableListOf<Node>()

    override fun add(node: Node) {
        sequence.add(node)
    }

    override fun build(): YamlSequence = sequence
}

fun sequence(block: YamlSequenceBuilder.() -> Unit): YamlSequence = YamlSequenceBuilder().apply(block).build()
