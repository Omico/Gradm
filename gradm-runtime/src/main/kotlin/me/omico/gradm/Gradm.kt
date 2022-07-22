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

import me.omico.gradm.path.GradleRootProjectPaths
import me.omico.gradm.path.buildSourceProjectPaths
import me.omico.gradm.path.gitIgnoreFile
import me.omico.gradm.path.gradmConfigFile
import me.omico.gradm.path.gradmGeneratedDependenciesProjectPaths
import me.omico.gradm.path.sourceFolder
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.readText

const val GRADM_DEPENDENCY_PACKAGE_NAME = "me.omico.gradm.dependency"

val hasGradmConfig: Boolean
    get() = gradmConfigFile.exists()

val shouldIgnoredByGit: Boolean
    get() = when (GradmConfigs.mode) {
        GradmMode.Normal -> !GradleRootProjectPaths.gitIgnoreFile.hasIgnoredGradmGeneratedFolder
        GradmMode.BuildSource -> !GradleRootProjectPaths.buildSourceProjectPaths.gitIgnoreFile.hasIgnoredGradmGeneratedFolder
    }

private val Path.hasIgnoredGradmGeneratedFolder: Boolean
    get() = exists() && ".gradm" in readText()

val isGradmGeneratedDependenciesSourcesExists: Boolean
    get() = gradmGeneratedDependenciesProjectPaths.sourceFolder.exists()

fun info(message: () -> String) {
    println("[Gradm] ${message()}")
}

fun debug(message: () -> String) {
    if (GradmConfigs.debug) info(message)
}
