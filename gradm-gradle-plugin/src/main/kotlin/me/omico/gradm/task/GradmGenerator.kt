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
package me.omico.gradm.task

import me.omico.gradm.internal.asYamlDocument
import me.omico.gradm.internal.codegen.generateDependenciesSourceFiles
import me.omico.gradm.internal.codegen.generatePluginSourceFiles
import me.omico.gradm.internal.codegen.generateSelfSourceFiles
import me.omico.gradm.internal.codegen.generateVersionsSourceFile
import me.omico.gradm.internal.config.format.formatGradmConfig
import me.omico.gradm.internal.maven.resolveVersionsMeta
import me.omico.gradm.path.gradmProjectPaths
import me.omico.gradm.utility.clearDirectory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

abstract class GradmGenerator : GradmTask() {

    abstract val configFileProperty: RegularFileProperty
        @InputFile get

    abstract val outputDirectoryProperty: DirectoryProperty
        @OutputDirectory get

    @TaskAction
    fun generate() {
        val gradmConfigFile = configFileProperty.get().asFile.toPath()
        val outputDirectory = outputDirectoryProperty.asFile.get().toPath()
        val gradmProjectPaths = project.gradmProjectPaths
        formatGradmConfig(gradmConfigFile)
        outputDirectory.clearDirectory()
        val document = gradmConfigFile.asYamlDocument()
        val versionsMeta = resolveVersionsMeta(gradmProjectPaths, document)
        generateDependenciesSourceFiles(outputDirectory, document, versionsMeta)
        generateVersionsSourceFile(gradmProjectPaths, outputDirectory, document)
        generatePluginSourceFiles(outputDirectory, document)
        generateSelfSourceFiles(gradmProjectPaths, outputDirectory)
    }
}
