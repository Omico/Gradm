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

import me.omico.gradm.internal.GradmExtensionImpl
import me.omico.gradm.path.buildFolder
import me.omico.gradm.path.generatedDependenciesFolder
import me.omico.gradm.path.gradmGeneratedDependenciesProjectPaths
import me.omico.gradm.path.gradmProjectPaths
import me.omico.gradm.path.metadataFolder
import me.omico.gradm.path.sourceFolder
import me.omico.gradm.path.updatesFolder
import me.omico.gradm.path.versionsMetaHashFile
import me.omico.gradm.task.GradmDependenciesAnalysis
import me.omico.gradm.task.GradmUpdateDependencies
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.initialization.Settings
import org.gradle.api.invocation.Gradle
import org.gradle.api.tasks.Delete
import org.gradle.kotlin.dsl.register

class GradmPlugin : Plugin<Settings> {

    override fun apply(target: Settings) {
        GradmExtensionImpl.create(target)
        if (!GradmConfigs.enabled) {
            debug { "Gradm is manually disabled, skipping." }
            return
        }
        target.initializeGradmConfigs()
        if (!hasGradmConfig) {
            debug { "No gradm.yml found, skipping." }
            return
        }
        debug { "Debug mode enabled." }
        debug { "Gradm version is $GRADM_VERSION" }
        debug { "mode: ${GradmConfigs.mode}" }
        target.gradle.initializeGradm()
    }

    private fun Settings.initializeGradmConfigs() {
        GradmConfigs.rootDir = when (rootDir.name) {
            "buildSrc" -> rootDir.parentFile.toPath().also { GradmConfigs.mode = GradmMode.BuildSource }
            else -> rootDir.toPath()
        }
        if (GradmConfigs.mode == GradmMode.Unspecified) {
            GradmConfigs.mode = when (gradle.parent) {
                null -> GradmMode.Normal
                else -> GradmMode.BuildLogic
            }
        }
        GradmConfigs.offline = gradle.startParameter.isOffline
        GradmConfigs.requireRefresh = !isGradmGeneratedDependenciesSourcesExists
    }

    private fun Gradle.initializeGradm() {
        when (GradmConfigs.mode) {
            GradmMode.Normal -> initializeGradmForNormalMode()
            GradmMode.BuildSource -> initializeGradmForBuildSourceMode()
            GradmMode.BuildLogic -> initializeGradmForBuildLogicMode()
            GradmMode.Unspecified -> return
        }
    }

    private fun Gradle.initializeGradmForNormalMode() {
        val hash = gradmProjectPaths.versionsMetaHashFile.asVersionsMetaHash() ?: "null"
        settingsEvaluated {
            initializeGradmFiles()
            includeBuild(gradmProjectPaths.generatedDependenciesFolder) {
                dependencySubstitution {
                    substitute(module("me.omico.gradm:gradm-generated-dependencies:$hash")).using(project(":"))
                }
            }
            pluginManagement {
                plugins {
                    gradmDeclaredPlugins.forEach { plugin ->
                        id(plugin.id).version(plugin.version)
                    }
                }
            }
        }
        beforeProject {
            if (this != rootProject) return@beforeProject
            buildscript.dependencies.add(
                "classpath",
                "me.omico.gradm:gradm-generated-dependencies:$hash",
            )
            registerGradmTasks()
        }
    }

    private fun Gradle.initializeGradmForBuildSourceMode() {
        settingsEvaluated {
            initializeGradmFiles()
            include(":generated-dependencies")
            project(":generated-dependencies").projectDir = gradmProjectPaths.generatedDependenciesFolder.toFile()
        }
        afterProject {
            if (this != rootProject) return@afterProject
            dependencies.add("implementation", project(":generated-dependencies"))
            gradmDeclaredPlugins.forEach { plugin ->
                dependencies.add("implementation", "${plugin.module}:${plugin.version}")
            }
            registerGradmTasks()
        }
    }

    private fun Gradle.initializeGradmForBuildLogicMode() {
        settingsEvaluated {
            initializeGradmFiles()
            include(":generated-dependencies")
            project(":generated-dependencies").projectDir = gradmProjectPaths.generatedDependenciesFolder.toFile()
        }
        afterProject {
            if (this == rootProject) {
                registerGradmTasks()
                return@afterProject
            }
            if (name == "generated-dependencies") return@afterProject
            dependencies.add("implementation", project(":generated-dependencies"))
            gradmDeclaredPlugins.forEach { plugin ->
                dependencies.add("implementation", "${plugin.module}:${plugin.version}")
            }
        }
    }

    private fun Project.registerGradmTasks() {
        tasks.register("gradmCheckGitIgnore") {
            group = "gradm"
            if (shouldIgnoredByGit) doLast {
                logger.warn("The generated directory \".gradm\" should be ignored by Git.")
            }
        }
        tasks.register("gradmUpdateDependencies", GradmUpdateDependencies::class) {
            group = "gradm"
            finalizedBy("gradmCheckGitIgnore")
        }
        if (GradmConfigs.mode == GradmMode.Normal) tasks.register("gradmDependenciesAnalysis", GradmDependenciesAnalysis::class) {
            group = "gradm"
        }
        tasks.register("gradmClean", Delete::class) {
            group = "gradm"
            with(gradmProjectPaths) {
                delete(metadataFolder)
                delete(updatesFolder)
            }
            with(gradmGeneratedDependenciesProjectPaths) {
                delete(sourceFolder)
                delete(buildFolder)
            }
        }
    }
}
