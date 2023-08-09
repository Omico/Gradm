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

import me.omico.gradm.GradmExtension
import me.omico.gradm.internal.YamlDocument
import me.omico.gradm.internal.asYamlDocument
import me.omico.gradm.internal.config.format.formatGradmConfiguration
import me.omico.gradm.path.GradmProjectPaths
import me.omico.gradm.path.gradmConfigurationFile
import me.omico.gradm.service.GradmWorkerService
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.file.ProjectLayout
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

abstract class GradmTask : DefaultTask() {
    @get:Inject
    abstract val projectLayout: ProjectLayout

    @get:Internal
    abstract val offlineProperty: Property<Boolean>

    @get:Internal
    abstract val workerServiceProperty: Property<GradmWorkerService>

    @get:Internal
    abstract val projectNameProperty: Property<String>

    @get:[
    InputFile
    PathSensitive(PathSensitivity.ABSOLUTE)
    ]
    abstract val configurationFileProperty: RegularFileProperty

    init {
        group = GROUP
    }

    @get:Internal
    protected val offline: Boolean
        get() = offlineProperty.get()

    @get:Internal
    protected val workerService: GradmWorkerService
        get() = workerServiceProperty.get()

    @get:Internal
    protected val gradmProjectPaths: GradmProjectPaths
        get() = GradmProjectPaths(
            path = projectLayout.projectDirectory.asFile.toPath(),
            configurationFile = configurationFileProperty.get().asFile.toPath(),
            projectName = projectNameProperty.get(),
        )

    @get:Internal
    protected val gradmConfigurationDocument: YamlDocument
        get() = run {
            val gradmConfigurationFile = gradmProjectPaths.configurationFile
            formatGradmConfiguration(gradmConfigurationFile)
            gradmConfigurationFile.asYamlDocument()
        }

    @TaskAction
    protected open fun execute() {
        workerService.update(
            offline = offline,
            gradmConfigurationDocument = gradmConfigurationDocument,
        )
    }

    internal fun Project.setupGradmTask(
        gradmExtension: GradmExtension,
        gradmWorkerServiceProvider: Provider<GradmWorkerService>,
    ) {
        usesService(gradmWorkerServiceProvider)
        offlineProperty.set(gradle.startParameter.isOffline)
        workerServiceProperty.set(gradmWorkerServiceProvider)
        projectNameProperty.set(gradmExtension.projectName)
        configurationFileProperty.set(projectLayout.gradmConfigurationFile(gradmExtension.configurationFilePath))
    }

    companion object {
        private const val GROUP = "gradm"
    }
}
