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
import me.omico.gradm.task.GradmDependenciesAnalysis
import me.omico.gradm.task.GradmUpdateDependencies
import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import org.gradle.api.tasks.Delete
import org.gradle.kotlin.dsl.register

class GradmPlugin : Plugin<Settings> {

    override fun apply(target: Settings) {
        target.initializeGradmConfig()
        if (!hasGradmConfig) {
            debug { "No gradm.yml found, skipping." }
            return
        }
        GradmExtensionImpl.create(target)
        val result = GradmParser.execute()
        target.includeBuild(gradmProjectPaths.generatedDependenciesFolder) {
            dependencySubstitution {
                substitute(module("me.omico.gradm:gradm-generated-dependencies")).using(project(":"))
            }
        }
        target.gradle.settingsEvaluated {
            pluginManagement {
                plugins {
                    result.plugins.forEach { plugin ->
                        id(plugin.id).version(plugin.version)
                    }
                }
            }
        }
        target.gradle.beforeProject {
            if (this != rootProject) return@beforeProject
            buildscript.dependencies.add(
                "classpath",
                "me.omico.gradm:gradm-generated-dependencies",
            )
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
            tasks.register("gradmDependenciesAnalysis", GradmDependenciesAnalysis::class) {
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

    private fun Settings.initializeGradmConfig() {
        GradmConfigs.rootDir = rootDir.toPath()
        GradmConfigs.offline = gradle.startParameter.isOffline
        GradmConfigs.updateDependencies = !isGradmGeneratedDependenciesSourcesExists
    }
}
