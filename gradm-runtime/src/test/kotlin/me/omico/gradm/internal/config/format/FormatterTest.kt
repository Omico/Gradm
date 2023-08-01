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

import me.omico.gradm.internal.config.format.node.mapping
import me.omico.gradm.internal.config.format.node.scalar
import me.omico.gradm.internal.config.format.node.sequence
import me.omico.gradm.test.resources
import org.junit.jupiter.api.Test
import kotlin.io.path.readText

class FormatterTest {
    @Test
    fun `test YAML DSL builder`() {
        val content = yaml {
            comment("Top comment")
            newline()
            scalar("test1", "normal")
            scalar("test2", "with quotes", ScalarStyle.DoubleQuoted)
            comments("comment1", "comment2")
            mapping("test3") {
                scalar("one", 1)
                mapping("two") {
                    scalar("three", 3)
                    mapping("two") {
                        scalar("three", 3)
                    }
                }
                sequence("four") {
                    scalar("aaaa")
                    mapping {
                        scalar("xxx", "xx")
                        scalar("yyy", "yy")
                    }
                    mapping {
                        scalar(
                            key = "xxx",
                            value = "xx",
                            comment = Comment(
                                block = listOf("comment"),
                                inline = "comment",
                            ),
                        )
                        scalar("yyy", "yy")
                        scalar("aa", "yy")
                    }
                }
            }
            comment("End comment")
        }
        assert(content == resources("format-test.yml").readText())
    }

    @Test
    fun `test format Gradm configuration`() {
        val unformattedConfigurationFile = resources("gradm.unformatted.yml")
        val formattedConfigurationFile = resources("gradm.formatted.yml")
        assert(unformattedConfigurationFile.createFormattedGradmConfigurationContent() == formattedConfigurationFile.readText())
    }
}
