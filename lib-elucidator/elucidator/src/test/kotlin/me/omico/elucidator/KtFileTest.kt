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

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import kotlin.io.path.readText

class KtFileTest {

    @Test
    fun test(@TempDir tempDir: Path) {
        ktFile("hello", "World") {
            addFunction("test") {
                addAnnotation<Suppress> {
                    addMember("%S", "unused")
                }
                addParameter<String>("parameter1")
                addParameter<Any>("parameter2")
                returnType<Unit>()
            }
            writeTo(tempDir)
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
            |}
            |
            """.trimMargin()
        assert(expected == tempDir.resolve("hello/World.kt").readText().also(::println))
    }
}
