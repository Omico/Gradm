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

import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider
import java.nio.file.Path
import kotlin.io.path.div
import kotlin.io.path.name

@JvmInline
value class GradmProjectPaths(val path: Path)

inline val Project.gradmGeneratedSourcesDirectory: Provider<Directory>
    get() = layout.buildDirectory.dir("generated/sources/gradm/kotlin/main")

inline val Project.gradmProjectPaths: GradmProjectPaths
    get() = GradmProjectPaths(buildDir.toPath() / "gradm")

inline val GradmProjectPaths.projectDirectory: Path
    get() = (path / ".." / "..").normalize()

inline val GradmProjectPaths.projectName: String
    get() = projectDirectory.name

inline val GradmProjectPaths.integrationDirectory: Path
    get() = path / "integration"

inline val GradmProjectPaths.metadataDirectory: Path
    get() = path / "metadata"

inline val GradmProjectPaths.updatesDirectory: Path
    get() = path / "updates"

inline val GradmProjectPaths.updatesAvailableFile: Path
    get() = updatesDirectory / "available.yml"

inline val GradmProjectPaths.generatedJar: Path
    get() = (path / ".." / "libs" / "$projectName.jar").normalize()
