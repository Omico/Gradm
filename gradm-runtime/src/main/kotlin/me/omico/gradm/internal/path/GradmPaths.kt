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
package me.omico.gradm.internal.path

import java.nio.file.Path

object GradmPaths {

    val rootDir: Path by lazy { RootProjectPaths.rootDir.resolve(".gradm") }
    val integrationDir: Path by lazy { rootDir.resolve("integration") }

    object Metadata {
        val rootDir: Path by lazy { GradmPaths.rootDir.resolve("metadata") }
        val versionsMeta: Path by lazy { rootDir.resolve("versions-meta.txt") }
        val versionsMetaHash: Path by lazy { rootDir.resolve("versions-meta.hash") }
    }

    object Updates {
        val rootDir: Path by lazy { GradmPaths.rootDir.resolve("updates") }
        val available: Path by lazy { rootDir.resolve("available.yml") }
    }

    object GeneratedDependenciesProject : ProjectPaths(
        rootDir = rootDir.resolve("generated-dependencies"),
    ) {
        val sourceDir: Path by lazy { rootDir.resolve("src/main/kotlin") }
        val gradleBuildScript: Path by lazy { rootDir.resolve("build.gradle.kts") }
        val gradleSettingsScript: Path by lazy { rootDir.resolve("settings.gradle.kts") }
    }
}
