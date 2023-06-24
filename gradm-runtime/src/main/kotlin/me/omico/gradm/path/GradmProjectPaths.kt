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
package me.omico.gradm.path

import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.file.ProjectLayout
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.div

data class GradmProjectPaths(
    val path: Path,
    val configurationFile: Path,
    val projectName: String,
)

fun ProjectLayout.gradmConfigFile(path: String): RegularFile = projectDirectory.file(path)

inline val Project.gradmGeneratedSourcesDirectory: Provider<Directory>
    get() = layout.buildDirectory.dir("generated/sources/gradm/kotlin/main")

inline val GradmProjectPaths.buildDirectory: Path
    get() = path / "build"

inline val GradmProjectPaths.gradmBuildDirectory: Path
    get() = buildDirectory / "gradm"

inline val GradmProjectPaths.gradmLocalConfigurationFile: Path
    get() = gradmBuildDirectory / ".gradm"

inline val GradmProjectPaths.integrationDirectory: Path
    get() = gradmBuildDirectory / "integration"

inline val GradmProjectPaths.updatesDirectory: Path
    get() = gradmBuildDirectory / "updates"

inline val GradmProjectPaths.updatesAvailableFile: Path
    get() = updatesDirectory / "available.yml"

inline val GradmProjectPaths.generatedJar: Path
    get() = (buildDirectory / "libs" / "$projectName.jar").normalize()

val userHomeDirectory: Path = Path(System.getProperty("user.home"))
val gradmUserHomeDirectory: Path = userHomeDirectory / ".gradm"
val gradmMetadataFile: Path = gradmUserHomeDirectory / ".metadata"
