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
package me.omico.gradm.path

import me.omico.gradm.GradmConfigs
import java.nio.file.Path

interface GradleProjectPaths : ProjectPaths

fun GradleProjectPaths(path: Path): GradleProjectPaths = GradleProjectPathsImpl(path)

private class GradleProjectPathsImpl(override val path: Path) : GradleProjectPaths

interface GradleRootProjectPaths : GradleProjectPaths {
    companion object : GradleRootProjectPaths {
        override val path: Path = GradmConfigs.rootDir
    }
}

inline val GradleRootProjectPaths.gradleFolder: Path
    get() = path.resolve("gradle")

inline val GradleRootProjectPaths.gradleWrapperFolder: Path
    get() = gradleFolder.resolve("wrapper")

inline val GradleRootProjectPaths.gradleWrapperJar: Path
    get() = gradleWrapperFolder.resolve("gradle-wrapper.jar")

inline val GradleRootProjectPaths.gradleWrapperProperties: Path
    get() = gradleWrapperFolder.resolve("gradle-wrapper.properties")

inline val GradleRootProjectPaths.gradleWrapperScript: Path
    get() = path.resolve("gradlew")

inline val GradleRootProjectPaths.gradleWrapperBatScript: Path
    get() = path.resolve("gradlew.bat")

inline val GradleProjectPaths.gradleBuildScript: Path
    get() = path.resolve("build.gradle.kts")

inline val GradleProjectPaths.gradleSettingsScript: Path
    get() = path.resolve("settings.gradle.kts")

inline val GradleProjectPaths.buildFolder: Path
    get() = path.resolve("build")

inline val GradleProjectPaths.buildSourceFolder: Path
    get() = path.resolve("buildSrc")

inline val GradleProjectPaths.buildSourceProjectPaths: GradleProjectPaths
    get() = GradleProjectPaths(buildSourceFolder)

inline val GradleProjectPaths.sourceFolder: Path
    get() = path.resolve("src/main/kotlin")
