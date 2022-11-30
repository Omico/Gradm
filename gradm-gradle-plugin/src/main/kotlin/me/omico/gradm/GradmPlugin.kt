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
@file:Suppress("unused")

package me.omico.gradm

import me.omico.gradm.integration.GradmIntegrationsExtension
import me.omico.gradm.integration.internal.GradmIntegrationsExtensionImpl
import me.omico.gradm.internal.GradmExtensionImpl
import me.omico.gradm.internal.GradmFormatExtensionImpl
import me.omico.gradm.path.gradmGeneratedSourcesDirectory
import me.omico.gradm.task.GradmDependencyUpdates
import me.omico.gradm.task.GradmGenerator
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.kotlin
import org.gradle.kotlin.dsl.plugins.base.KotlinDslBasePlugin
import org.gradle.kotlin.dsl.register
import org.gradle.plugin.devel.GradlePluginDevelopmentExtension
import org.gradle.plugin.devel.plugins.JavaGradlePluginPlugin

class GradmPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        apply<JavaGradlePluginPlugin>()
        apply<KotlinDslBasePlugin>()
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
        dependencies.add("compileOnly", dependencies.kotlin("gradle-plugin"))
        configureGradmGenerator(gradmExtension)
        configureGradmDependencyUpdates(gradmExtension)
        GradmConfiguration.offline = gradle.startParameter.isOffline
    }
}

private fun Project.configureGradmGenerator(gradmExtension: GradmExtension) {
    val generateGradmSources = tasks.register<GradmGenerator>("generateGradmSources") {
        configFileProperty.convention { file(gradmExtension.configFilePath) }
        outputDirectoryProperty.convention(gradmGeneratedSourcesDirectory)
    }
    val sourceSets = extensions.getByType<SourceSetContainer>()
    sourceSets["main"].java.srcDir(generateGradmSources.flatMap(GradmGenerator::outputDirectoryProperty))
}

private fun Project.configureGradmDependencyUpdates(gradmExtension: GradmExtension) {
    tasks.register<GradmDependencyUpdates>("gradmDependencyUpdates") {
        configFileProperty.convention { file(gradmExtension.configFilePath) }
        finalizedBy(tasks["generateGradmSources"])
    }
}
