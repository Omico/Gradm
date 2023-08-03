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
package me.omico.gradm.task

import me.omico.gradm.GradmExtension
import me.omico.gradm.service.GradmWorkerService
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.provider.Provider
import javax.inject.Inject

abstract class GradmDependenciesTask : GradmTask() {
    @get:Inject
    abstract val dependencies: DependencyHandler

    init {
        notCompatibleWithConfigurationCache()
    }

    override fun execute() {
        super.execute()
        workerService.update(
            dependencies = dependencies,
        )
    }

    internal fun Project.setupGradmDependenciesTask(
        gradmExtension: GradmExtension,
        gradmWorkerServiceProvider: Provider<GradmWorkerService>,
    ) {
        dependsOn(GradmInitialization.TASK_NAME)
        setupGradmTask(gradmExtension, gradmWorkerServiceProvider)
    }

    private fun notCompatibleWithConfigurationCache() =
        notCompatibleWithConfigurationCache(
            "[Gradm] This task is not compatible with the Gradle configuration cache.\n" +
                "Waiting for https://github.com/gradle/gradle/issues/13506 to be fixed.",
        )
}
