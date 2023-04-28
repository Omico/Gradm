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
package me.omico.gradm.test.config.format

import me.omico.gradm.internal.config.format.Comment
import me.omico.gradm.internal.config.format.ScalarStyle
import me.omico.gradm.internal.config.format.comments
import me.omico.gradm.internal.config.format.createFormattedGradmConfigContent
import me.omico.gradm.internal.config.format.node.mapping
import me.omico.gradm.internal.config.format.node.scalar
import me.omico.gradm.internal.config.format.node.sequence
import me.omico.gradm.internal.config.format.yaml
import org.junit.jupiter.api.Test
import java.nio.file.Paths
import kotlin.io.path.readText

class FormatterTest {

    private val testResourcesPath = Paths.get("src", "test", "resources")

    @Test
    fun format() {
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
        assert(content == testResourcesPath.resolve("format-test.yml").readText())
    }

    @Test
    fun formatGradmConfigFile() {
        val unformattedConfig = testResourcesPath.resolve("gradm.unformatted.yml")
        val formattedConfig = testResourcesPath.resolve("gradm.formatted.yml")
        assert(unformattedConfig.createFormattedGradmConfigContent() == formattedConfig.readText())
    }
}
