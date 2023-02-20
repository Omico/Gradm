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
package me.omico.gradm.internal.codegen

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ProjectsTest {

    @Test
    fun `test NestedProject`() {
        val projects = listOf(
            ":app",
            ":core:common",
            ":core:data",
            ":core:data:common",
            ":core:data:repository",
            ":core:ui:theme",
            ":core:data:repository:database",
            ":core:data:repository:network",
        )
        val rootNestedProject = NestedProject.Root
        projects.forEach(rootNestedProject::add)
        val testRootNestedProject = rootNestedProject.copy(
            subProjects = NestedProjects().apply {
                this["app"] = NestedProject("app", ":app")
                this["core"] = NestedProject(
                    name = "core",
                    subProjects = NestedProjects().apply {
                        this["common"] = NestedProject("common", ":core:common")
                        this["data"] = NestedProject(
                            name = "data",
                            path = ":core:data",
                            subProjects = NestedProjects().apply {
                                this["common"] = NestedProject("common", ":core:data:common")
                                this["repository"] = NestedProject(
                                    name = "repository",
                                    path = ":core:data:repository",
                                    subProjects = NestedProjects().apply {
                                        this["database"] = NestedProject("database", ":core:data:repository:database")
                                        this["network"] = NestedProject("network", ":core:data:repository:network")
                                    },
                                )
                            },
                        )
                        this["ui"] = NestedProject(
                            name = "ui",
                            subProjects = NestedProjects().apply {
                                this["theme"] = NestedProject("theme", ":core:ui:theme")
                            },
                        )
                    },
                )
            },
        )
        assertEquals(rootNestedProject, testRootNestedProject)
    }
}
