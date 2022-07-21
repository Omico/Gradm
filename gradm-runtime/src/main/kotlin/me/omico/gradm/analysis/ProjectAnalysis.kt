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
package me.omico.gradm.analysis

import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ExternalModuleDependency

data class ProjectAnalysisResult(
    val project: Project,
    val configurationDependencies: ConfigurationDependencies,
)

typealias ConfigurationDependencies = Map<String, Dependencies>
typealias Dependencies = List<String>

fun Project.analyseAllDependencies(): List<ProjectAnalysisResult> =
    rootProject.allprojects.map(Project::analyseDependencies)

fun Project.analyseDependencies(): ProjectAnalysisResult =
    ProjectAnalysisResult(this, collectConfigurationDependencies())

fun List<ProjectAnalysisResult>.combineAsDependencies(): Dependencies =
    flatMap { it.configurationDependencies.values }
        .flatten()
        .distinct()
        .sorted()

internal fun Project.collectConfigurationDependencies(): ConfigurationDependencies =
    configurations.associate { configuration -> configuration.name to configuration.collectDependencies() }

internal fun Configuration.collectDependencies(): Dependencies =
    dependencies
        .filterIsInstance<ExternalModuleDependency>()
        .map { "${it.group}:${it.name}:${it.version}" }
