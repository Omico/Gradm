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
package me.omico.gradm

import me.omico.gradm.utility.gradleCommand
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.junit.jupiter.api.assertDoesNotThrow

@TestMethodOrder(MethodOrderer.MethodName::class)
class IntegrationTesting {

    @Test
    fun `test examples gradm-getting-started`() {
        assertDoesNotThrow {
            gradleCommand(
                directory = "../examples/gradm-getting-started",
                arguments = arrayOf(
                    "spotlessApply", "clean",
                    "gradmClean", "gradmUpdateDependencies",
                    "build", "--no-daemon",
                ),
            )
        }
    }

    @Test
    fun `test examples gradm-with-build-logic`() {
        assertDoesNotThrow {
            gradleCommand(
                directory = "../examples/gradm-with-build-logic",
                arguments = arrayOf(
                    "spotlessApply", "clean",
                    ":build-logic:gradmClean", ":build-logic:gradmUpdateDependencies",
                    "build", "--no-daemon",
                ),
            )
        }
    }

    @Test
    fun `test examples gradm-with-buildSrc`() {
        assertDoesNotThrow {
            gradleCommand(
                directory = "../examples/gradm-with-buildSrc",
                arguments = arrayOf("-p", "buildSrc", "gradmClean", "gradmUpdateDependencies", "--no-daemon"),
            )
            gradleCommand(
                directory = "../examples/gradm-with-buildSrc",
                arguments = arrayOf("clean", "build", "--no-daemon"),
            )
        }
    }
}
