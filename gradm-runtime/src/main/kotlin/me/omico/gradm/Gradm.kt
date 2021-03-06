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

import me.omico.gradm.internal.path.GradmPaths
import me.omico.gradm.internal.path.RootProjectPaths
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.readText

const val GRADM_DEPENDENCY_PACKAGE_NAME = "me.omico.gradm.dependency"

val projectRootDir: Path
    get() = RootProjectPaths.rootDir

val hasGradmConfig: Boolean
    get() = RootProjectPaths.gradmConfig.exists()

val shouldIgnoredByGit: Boolean
    get() = RootProjectPaths.gitIgnore.let { it.exists() && ".gradm" !in it.readText() }

val isGradmGeneratedDependenciesSourcesExists: Boolean
    get() = GradmPaths.GeneratedDependenciesProject.sourceDir.exists()

val gradmMetadataDir: Path
    get() = GradmPaths.Metadata.rootDir

val gradmUpdatesDir: Path
    get() = GradmPaths.Updates.rootDir

val gradmGeneratedDependenciesDir: Path
    get() = GradmPaths.GeneratedDependenciesProject.rootDir

val gradmGeneratedDependenciesBuildDir: Path
    get() = GradmPaths.GeneratedDependenciesProject.buildDir

val gradmGeneratedDependenciesSourceDir: Path
    get() = GradmPaths.GeneratedDependenciesProject.sourceDir

fun debug(message: () -> String) {
    if (GradmConfigs.debug) println(message())
}
