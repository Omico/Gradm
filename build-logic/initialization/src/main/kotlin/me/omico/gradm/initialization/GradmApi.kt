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
package me.omico.gradm.initialization

import org.gradle.api.initialization.Settings
import java.io.File

internal fun Settings.syncPropertiesToGradmApi() {
    settingsDir.resolve("gradle.properties")
        .copyTo(
            target = settingsDir.resolve("gradm-api/gradle.properties"),
            overwrite = true,
        )
    settingsDir.resolve("local.properties")
        .takeIf(File::exists)
        ?.copyTo(
            target = settingsDir.resolve("gradm-api/local.properties"),
            overwrite = true,
        )
}

internal fun Settings.includeGradmApi(path: String) {
    include(path)
    project(path).name = "gradm-api${path.replace(":", "-")}"
}

internal val Settings.gradmApiModules: Set<String>
    get() = settingsDir.resolve("gradm-api").walk()
        .maxDepth(1)
        .filter(File::isDirectory)
        .map(File::getName)
        .filterNot { it in ignoredList }
        .toSet()

private val ignoredList = listOf(
    "gradm-api",
    ".gradle",
    "build",
)
