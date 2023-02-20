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
import me.omico.gradm.path.gradmGeneratedSourcesDirectory
import me.omico.gradm.task.GradmDependencyUpdates
import me.omico.gradm.task.GradmGenerator
import me.omico.gradm.utility.internalIncludedBuilds
import me.omico.gradm.utility.requireKotlinDslPlugin
import me.omico.gradm.utility.rootGradle
import org.gradle.BuildAdapter
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.initialization.ProjectDescriptor
import org.gradle.api.initialization.Settings
import org.gradle.api.internal.project.ProjectState
import org.gradle.api.invocation.Gradle
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.registerIfAbsent
import org.gradle.plugin.devel.GradlePluginDevelopmentExtension
import java.io.File

class GradmPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        requireKotlinDslPlugin()
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
        val gradmWorkerServiceProvider = gradle.sharedServices.registerIfAbsent(
            name = "gradmWorkerService",
            implementationType = GradmWorkerService::class,
            configureAction = {},
        )
        configureGradmGenerator(gradmExtension, gradmWorkerServiceProvider)
        configureGradmDependencyUpdates(gradmExtension, gradmWorkerServiceProvider)
        GradmConfiguration.offline = gradle.startParameter.isOffline
        afterEvaluate {
            generateProjectInfoFile()
        }
    }
}

private fun Project.configureGradmGenerator(
    gradmExtension: GradmExtension,
    gradmWorkerServiceProvider: Provider<GradmWorkerService>,
) {
    val generateGradmSources = tasks.register<GradmGenerator>("generateGradmSources") {
        usesService(gradmWorkerServiceProvider)
        serviceProperty.set(gradmWorkerServiceProvider)
        configFileProperty.convention { file(gradmExtension.configFilePath) }
        outputDirectoryProperty.convention(gradmGeneratedSourcesDirectory)
    }
    val sourceSets = extensions.getByType<SourceSetContainer>()
    sourceSets["main"].java.srcDir(generateGradmSources.flatMap(GradmGenerator::outputDirectoryProperty))
}

private fun Project.configureGradmDependencyUpdates(
    gradmExtension: GradmExtension,
    gradmWorkerServiceProvider: Provider<GradmWorkerService>,
) {
    tasks.register<GradmDependencyUpdates>("gradmDependencyUpdates") {
        serviceProperty.set(gradmWorkerServiceProvider)
        usesService(gradmWorkerServiceProvider)
        configFileProperty.convention { file(gradmExtension.configFilePath) }
    }
}

private fun Project.generateProjectInfoFile() {
    if (!GradmExperimentalConfiguration.typesafeProjectAccessorsSubstitution) return
    experimentalInfo("typesafe project accessors substitution")
    val projectInfoFile = layout.buildDirectory.file("gradm/project-info.txt").get().asFile
    rootGradle.addBuildListener(GradmProjectCollector(projectInfoFile))
}

private class GradmProjectCollector(
    private val projectInfoFile: File,
) : BuildAdapter() {
    private val paths = HashSet<String>()

    init {
        if (projectInfoFile.parentFile.exists().not()) projectInfoFile.parentFile.mkdirs()
        if (projectInfoFile.exists()) projectInfoFile.delete()
    }

    override fun settingsEvaluated(settings: Settings) {
        val settingsInternal = settings as org.gradle.api.internal.SettingsInternal
        val projects = settingsInternal.projectRegistry.allProjects.map(ProjectDescriptor::getPath)
        paths.addAll(projects)
    }

    override fun projectsLoaded(gradle: Gradle) {
        gradle.internalIncludedBuilds.forEach { includedBuild ->
            includedBuild.target.projects.rootProject.run {
                addChildProjectPaths(this)
            }
        }
        paths
            .filterNot { it == ":" || it.isBlank() }
            .sorted()
            .joinToString("\n")
            .let(projectInfoFile::writeText)
    }

    private fun ProjectState.addChildProjectPaths(rootProject: ProjectState) {
        childProjects.forEach { child -> child.addChildProjectPaths(rootProject) }
        identityPath.toString().removePrefix(rootProject.identityPath.toString()).let(paths::add)
    }
}
