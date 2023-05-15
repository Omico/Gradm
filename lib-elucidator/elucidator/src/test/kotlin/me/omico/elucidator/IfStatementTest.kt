/*
 * Copyright 2023 Omico
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
package me.omico.elucidator

import java.nio.file.Path
import kotlin.io.path.createTempDirectory
import kotlin.io.path.readText
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class IfStatementTest {
    private val tempDirectory: Path = createTempDirectory()

    @Test
    fun `test if statement is declared correctly`() {
        TestFunctionScope.addIfStatement {
            start("parameter1 == %S", "hello") {
                addComment("TODO if")
            }
            then("parameter2 is %T", String::class) {
                addComment("TODO else if")
            }
            final {
                addComment("TODO else")
            }
        }
        assertFailsWith<IllegalStateException>("start() must be called only once.") {
            TestFunctionScope.addIfStatement {
                start("parameter1 == %S", "hello") {
                    addComment("TODO if")
                }
                start("parameter1 == %S", "hello") {
                    addComment("TODO if")
                }
            }
        }
        assertFailsWith<IllegalStateException>("start() must be called before then().") {
            TestFunctionScope.addIfStatement {
                then("parameter2 is %T", String::class) {
                    addComment("TODO else if")
                }
            }
        }
        assertFailsWith<IllegalStateException>("start() must be called before final().") {
            TestFunctionScope.addIfStatement {
                final {
                    addComment("TODO else")
                }
            }
        }
    }

    @Test
    fun `test output`() {
        ktFile("hello", "World") {
            addFunction("test") {
                addAnnotation<Suppress> {
                    addMember("%S", "unused")
                }
                addParameter<String>("parameter1")
                addParameter<Any>("parameter2")
                addIfStatement {
                    start("parameter1 == %S", "hello") {
                        addComment("TODO if")
                    }
                    then("parameter2 is %T", String::class) {
                        addComment("TODO else if")
                    }
                    final {
                        addComment("TODO else")
                    }
                }
            }
            writeTo(tempDirectory)
        }
        val expected =
            """
            |package hello
            |
            |import kotlin.Any
            |import kotlin.String
            |import kotlin.Suppress
            |import kotlin.Unit
            |
            |@Suppress("unused")
            |public fun test(parameter1: String, parameter2: Any): Unit {
            |  if (parameter1 == "hello") {
            |    // TODO if
            |  } else if (parameter2 is String) {
            |    // TODO else if
            |  } else {
            |    // TODO else
            |  }
            |}
            |
            """.trimMargin()
        assertEquals(expected, tempDirectory.resolve("hello/World.kt").readText())
    }
}
