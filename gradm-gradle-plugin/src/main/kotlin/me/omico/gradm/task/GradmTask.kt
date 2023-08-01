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
import me.omico.gradm.GradmWorkerService
import me.omico.gradm.internal.YamlDocument
import me.omico.gradm.internal.asYamlDocument
import me.omico.gradm.internal.config.format.formatGradmConfig
import me.omico.gradm.path.GradmProjectPaths
import me.omico.gradm.path.gradmConfigFile
import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.artifacts.dsl.RepositoryHandler
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
    abstract val projectNameProperty: Property<String>
        @Internal get

    abstract val workerServiceProperty: Property<GradmWorkerService>
        @Internal get

    abstract val configFileProperty: RegularFileProperty
        @PathSensitive(PathSensitivity.ABSOLUTE)
        @InputFile
        get

    abstract val projectLayout: ProjectLayout
        @Inject get

    abstract val repositories: RepositoryHandler
        @Inject get

    abstract val dependencies: DependencyHandler
        @Inject get

    final override fun getGroup(): String = "gradm"

    protected val workerService: GradmWorkerService
        @Internal get() = workerServiceProperty.get()

    protected val gradmProjectPaths: GradmProjectPaths
        @Internal get() = GradmProjectPaths(
            path = projectLayout.projectDirectory.asFile.toPath(),
            configurationFile = configFileProperty.get().asFile.toPath(),
            projectName = projectNameProperty.get(),
        )

    protected val gradmConfigDocument: YamlDocument
        @Internal get() = run {
            val gradmConfigFile = gradmProjectPaths.configurationFile
            formatGradmConfig(gradmConfigFile)
            gradmConfigFile.asYamlDocument()
        }

    @TaskAction
    protected open fun execute() {
        workerService.initialize(
            repositories = repositories,
            gradmProjectPaths = gradmProjectPaths,
            gradmConfigDocument = gradmConfigDocument,
        )
    }

    init {
        notCompatibleWithConfigurationCache()
    }

    private fun notCompatibleWithConfigurationCache() =
        notCompatibleWithConfigurationCache(
            "Gradm is not compatible with the Gradle configuration cache.\n" +
                "Waiting for https://github.com/gradle/gradle/issues/13506 to be fixed.",
        )
}

internal fun GradmTask.setup(
    gradmExtension: GradmExtension,
    gradmWorkerServiceProvider: Provider<GradmWorkerService>,
) {
    usesService(gradmWorkerServiceProvider)
    projectNameProperty.set(gradmExtension.projectName)
    workerServiceProperty.set(gradmWorkerServiceProvider)
    configFileProperty.set(projectLayout.gradmConfigFile(gradmExtension.configFilePath))
}
