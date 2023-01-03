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
package me.omico.gradm.internal.config.format.node

import me.omico.gradm.internal.config.format.Builder
import me.omico.gradm.internal.config.format.Comment
import me.omico.gradm.internal.config.format.ScalarStyle

typealias YamlMapping = Map<String, Node>

data class MappingNode(
    override val items: YamlMapping,
    override val comment: Comment = Comment(),
) : CollectionNode<YamlMapping>, YamlMapping by items

interface MappingNodeScope {
    fun put(key: String, node: Node)
}

fun <T : Any> MappingNodeScope.scalar(
    key: String,
    value: T,
    style: ScalarStyle = ScalarStyle.Plain,
    comment: Comment = Comment(),
) = put(key, ScalarNode(value, style, comment))

fun MappingNodeScope.sequence(
    key: String,
    values: YamlSequence,
    comment: Comment = Comment(),
) = put(key, SequenceNode(values, comment))

fun MappingNodeScope.sequence(
    key: String,
    comment: Comment = Comment(),
    block: YamlSequenceBuilder.() -> Unit,
) = sequence(key, sequence(block), comment)

fun MappingNodeScope.mapping(
    key: String,
    values: YamlMapping,
    comment: Comment = Comment(),
) = put(key, MappingNode(values, comment))

fun MappingNodeScope.mapping(
    key: String,
    comment: Comment = Comment(),
    block: YamlMappingBuilder.() -> Unit,
) = mapping(key, mapping(block), comment)

class YamlMappingBuilder : MappingNodeScope, Builder<YamlMapping> {

    private val mapping = LinkedHashMap<String, Node>()

    override fun put(key: String, node: Node) {
        require(!mapping.containsKey(key)) { "Duplicate key: $key" }
        mapping[key] = node
    }

    override fun build(): YamlMapping = mapping
}

fun mapping(block: YamlMappingBuilder.() -> Unit): YamlMapping = YamlMappingBuilder().apply(block).build()
