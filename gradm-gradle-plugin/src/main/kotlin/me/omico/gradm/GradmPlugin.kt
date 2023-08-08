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
@file:Suppress("unused")

package me.omico.gradm

import me.omico.gradm.integration.GradmIntegrationsExtension
import me.omico.gradm.integration.internal.GradmIntegrationsExtensionImpl
import me.omico.gradm.internal.GradmExperimentalExtensionImpl
import me.omico.gradm.internal.GradmExtensionImpl
import me.omico.gradm.internal.GradmFormatExtensionImpl
import me.omico.gradm.path.gradmAvailableUpdatesFile
import me.omico.gradm.path.gradmGeneratedSourcesDirectory
import me.omico.gradm.service.GradmWorkerService
import me.omico.gradm.service.registerGradmWorkerServiceIfAbsent
import me.omico.gradm.task.GradmDependencyUpdates
import me.omico.gradm.task.GradmInitialization
import me.omico.gradm.task.GradmSourcesGenerator
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.register
import org.gradle.plugin.devel.GradlePluginDevelopmentExtension

class GradmPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        require(plugins.hasPlugin("org.gradle.kotlin.kotlin-dsl")) {
            "Please add `kotlin-dsl` to your plugins block.\n" +
                "Gradm plugin requires the Kotlin DSL plugin to be applied."
        }
        val gradlePlugins = extensions.getByType<GradlePluginDevelopmentExtension>()
        val gradmExtension = extensions.create(
            publicType = GradmExtension::class,
            name = "gradm",
            instanceType = GradmExtensionImpl::class,
            constructionArguments = arrayOf(target, gradlePlugins),
        )
        gradmExtension.extensions.create(
            publicType = GradmFormatExtension::class,
            name = "format",
            instanceType = GradmFormatExtensionImpl::class,
        )
        gradmExtension.extensions.create(
            publicType = GradmIntegrationsExtension::class,
            name = "integrations",
            instanceType = GradmIntegrationsExtensionImpl::class,
        )
        gradmExtension.extensions.create(
            publicType = GradmExperimentalExtension::class,
            name = "experimental",
            instanceType = GradmExperimentalExtensionImpl::class,
        )
        val gradmWorkerServiceProvider = registerGradmWorkerServiceIfAbsent()
        configureGradmTasks(gradmExtension, gradmWorkerServiceProvider)
    }
}

private fun Project.configureGradmTasks(
    gradmExtension: GradmExtension,
    gradmWorkerServiceProvider: Provider<GradmWorkerService>,
) {
    tasks.register<GradmInitialization>(GradmInitialization.TASK_NAME) {
        setupGradmTask(gradmExtension, gradmWorkerServiceProvider)
    }
    tasks.register<GradmDependencyUpdates>(GradmDependencyUpdates.TASK_NAME) {
        setupGradmDependenciesTask(gradmExtension, gradmWorkerServiceProvider)
        availableUpdatesFileProperty.convention(gradmAvailableUpdatesFile)
    }
    val generateGradmSources = tasks.register<GradmSourcesGenerator>(GradmSourcesGenerator.TASK_NAME) {
        setupGradmDependenciesTask(gradmExtension, gradmWorkerServiceProvider)
        inputFiles.from(gradmAvailableUpdatesFile)
        gradmGeneratedSourcesDirectoryProperty.convention(gradmGeneratedSourcesDirectory)
    }
    val sourceSets = extensions.getByType<SourceSetContainer>()
    sourceSets["main"].java.srcDir(generateGradmSources.flatMap(GradmSourcesGenerator::gradmGeneratedSourcesDirectoryProperty))
}
