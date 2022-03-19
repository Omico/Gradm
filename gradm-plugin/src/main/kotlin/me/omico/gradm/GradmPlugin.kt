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

import me.omico.gradm.internal.ProjectConfig
import me.omico.gradm.task.GradmUpdateDependencies
import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import org.gradle.api.tasks.Delete
import org.gradle.kotlin.dsl.register

class GradmPlugin : Plugin<Settings> {

    override fun apply(target: Settings) {
        ProjectConfig.initialize(target)
        if (!hasGradmConfig) {
            println("No gradm.yml found, skipping.")
            return
        }
        target.gradle.run {
            settingsEvaluated {
                GradmParser.execute(updateDependencies = !isGradmGeneratedDependenciesSourcesExists)
                includeBuild(gradmGeneratedDependenciesDir) {
                    dependencySubstitution {
                        substitute(module("me.omico.gradm:gradm-generated-dependencies")).using(project(":"))
                    }
                }
            }
            beforeProject {
                if (this != rootProject) return@beforeProject
                buildscript.dependencies.add(
                    "classpath",
                    "me.omico.gradm:gradm-generated-dependencies",
                )
            }
            afterProject {
                if (this != rootProject) return@afterProject
                tasks.register("gradmUpdateDependencies", GradmUpdateDependencies::class)
                tasks.register("gradmClean", Delete::class) {
                    delete(gradmMetadataDir)
                    delete(gradmGeneratedDependenciesSourceDir)
                    delete(gradmGeneratedDependenciesBuildDir)
                }
            }
        }
    }
}
