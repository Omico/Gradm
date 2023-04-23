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
package me.omico.gradm.task

import me.omico.gradm.GradmWorkerService
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import java.nio.file.Path

abstract class GradmTask : DefaultTask() {
    abstract val workerServiceProperty: Property<GradmWorkerService>
        @Internal get

    abstract val configFileProperty: RegularFileProperty
        @PathSensitive(PathSensitivity.ABSOLUTE)
        @InputFile
        get

    final override fun getGroup(): String = "gradm"

    protected val workerService: GradmWorkerService
        @Internal get() = workerServiceProperty.get()

    protected val gradmConfigFile: Path
        @Internal get() = configFileProperty.get().asFile.toPath()

    init {
        notCompatibleWithConfigurationCache()
    }

    private fun notCompatibleWithConfigurationCache() =
        notCompatibleWithConfigurationCache("Gradm is not compatible with the Gradle configuration cache yet.")
}
