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
@file:Suppress("unused")

package me.omico.gradm.typesafe

import me.omico.gradm.path.gradmGeneratedSourcesDirectory
import me.omico.gradm.typesafe.internal.GradmTypesafeExtensionImpl
import me.omico.gradm.typesafe.task.GradmTypesafeGenerator
import me.omico.gradm.utility.internalIncludedBuilds
import me.omico.gradm.utility.requireKotlinDslPlugin
import me.omico.gradm.utility.rootGradle
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.internal.composite.IncludedBuildInternal
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.register
import org.gradle.plugin.devel.GradlePluginDevelopmentExtension
import java.io.File

class GradmTypesafePlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        requireKotlinDslPlugin()
        val gradlePlugins = extensions.getByType<GradlePluginDevelopmentExtension>()
        extensions.create(
            publicType = GradmTypesafeExtension::class,
            name = "gradmTypesafe",
            instanceType = GradmTypesafeExtensionImpl::class,
            constructionArguments = arrayOf(gradlePlugins),
        )
        val gradmProjects = rootGradle.internalIncludedBuilds.filter(IncludedBuildInternal::hasGradmPlugin)
        require(gradmProjects.isNotEmpty()) { "No Gradm project is found, cannot proceed." }
        require(gradmProjects.size == 1) { "More than one Gradm project is found, cannot proceed." }
        configureGradmTypesafeGenerator(gradmProjects.first().projectDir)
    }
}

private fun Project.configureGradmTypesafeGenerator(gradmProjectDir: File) {
    val projectInfoFile = File(gradmProjectDir, "build/gradm/project-info.txt")
    if (!projectInfoFile.exists()) return
    val generateGradmTypesafeSources = tasks.register<GradmTypesafeGenerator>("generateGradmTypesafeSources") {
        configFileProperty.convention { projectInfoFile }
        outputDirectoryProperty.convention(gradmGeneratedSourcesDirectory)
    }
    val sourceSets = extensions.getByType<SourceSetContainer>()
    sourceSets["main"].java.srcDir(generateGradmTypesafeSources.flatMap(GradmTypesafeGenerator::outputDirectoryProperty))
}

private val IncludedBuildInternal.hasGradmPlugin: Boolean
    get() = runCatching { target.projects.rootProject.mutableModel.plugins.hasPlugin("me.omico.gradm") }
        .getOrDefault(false)
