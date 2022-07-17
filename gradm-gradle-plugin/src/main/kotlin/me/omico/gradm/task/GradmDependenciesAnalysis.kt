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
package me.omico.gradm.task

import me.omico.gradm.analysis.analyseAllDependencies
import me.omico.gradm.analysis.combineAsDependencies
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.work.DisableCachingByDefault

@DisableCachingByDefault
abstract class GradmDependenciesAnalysis : DefaultTask() {
    @TaskAction
    protected fun execute() = with(project.analyseAllDependencies()) {
        println("Each project's dependencies:")
        println()
        filter { result -> result.configurationDependencies.isNotEmpty() }
            .forEach { result ->
                println(result.project.name)
                result.configurationDependencies
                    .filter { entry -> entry.value.isNotEmpty() }
                    .forEach { (configuration, dependencies) ->
                        println("  $configuration")
                        dependencies.forEach { dependency ->
                            println("    $dependency")
                        }
                    }
            }
        println()
        println()
        println("All dependencies:")
        println()
        combineAsDependencies().forEach { println(it) }
    }
}
