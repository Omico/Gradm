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

import java.nio.file.Path
import kotlin.io.path.copyTo
import kotlin.io.path.createDirectories

@JvmInline
value class GradmProjectPaths(override val path: Path) : GradleRootProjectPaths

inline val gradmConfigFile: Path
    get() = GradleRootProjectPaths.path.resolve("gradm.yml")

inline val gradmProjectPaths: GradmProjectPaths
    get() = GradmProjectPaths(path = GradleRootProjectPaths.path.resolve(".gradm"))

inline val GradmProjectPaths.integrationFolder: Path
    get() = path.resolve("integration")

inline val GradmProjectPaths.metadataFolder: Path
    get() = path.resolve("metadata")

inline val GradmProjectPaths.versionsMetaHashFile: Path
    get() = metadataFolder.resolve("versions-meta.hash")

inline val GradmProjectPaths.generatedDependenciesFolder: Path
    get() = path.resolve("generated-dependencies")

inline val gradmGeneratedDependenciesProjectPaths: GradleProjectPaths
    get() = GradleProjectPaths(gradmProjectPaths.generatedDependenciesFolder)

inline val GradmProjectPaths.updatesFolder: Path
    get() = path.resolve("updates")

inline val GradmProjectPaths.updatesAvailableFile: Path
    get() = updatesFolder.resolve("available.yml")

fun GradleRootProjectPaths.copyGradleWrapperToGradmFolder() {
    val gradmProjectPaths = GradmProjectPaths(path = path.resolve(".gradm"))
    gradmProjectPaths.path.createDirectories()
    gradmProjectPaths.gradleWrapperFolder.createDirectories()
    gradleWrapperJar.copyTo(gradmProjectPaths.gradleWrapperJar, overwrite = true)
    gradleWrapperProperties.copyTo(gradmProjectPaths.gradleWrapperProperties, overwrite = true)
    gradleWrapperScript.copyTo(gradmProjectPaths.gradleWrapperScript, overwrite = true)
    gradleWrapperBatScript.copyTo(gradmProjectPaths.gradleWrapperBatScript, overwrite = true)
}
