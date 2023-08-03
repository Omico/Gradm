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
package me.omico.gradm.integration.internal

import me.omico.gradm.integration.GradmIntegration
import me.omico.gradm.integration.GradmIntegrationExtension
import me.omico.gradm.integration.GradmIntegrationHolder
import me.omico.gradm.internal.YamlDocument
import me.omico.gradm.internal.asYamlDocument
import me.omico.gradm.internal.config.MutableFlatVersions
import me.omico.gradm.path.GradmProjectPaths
import me.omico.gradm.path.integrationRootDirectory
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.createFile
import kotlin.io.path.exists
import kotlin.io.path.notExists

internal class GradmIntegrationHolderImpl(
    extension: GradmIntegrationExtension,
    private val integration: GradmIntegration,
    private val inputPaths: Set<Path>,
    private val outputPaths: Set<Path>,
    override val gradmProjectPaths: GradmProjectPaths,
    override val versions: MutableFlatVersions,
) : GradmIntegrationHolder {
    private val integrationDirectory: Path =
        gradmProjectPaths.integrationRootDirectory.resolve(extension.id).createDirectories()

    override val attributes: Map<String, Any> = extension.attributes

    override val integrationConfiguration: YamlDocument =
        gradmProjectPaths.path.resolve(extension.configurationFilePath).takeIf(Path::exists)?.asYamlDocument()
            ?: error("Integration configuration file [${extension.configurationFilePath}] for [${extension.id}] does not exist.")

    override fun input(path: String): Path {
        val input = integrationDirectory.resolve(path)
        require(input in inputPaths) {
            "[$path] is not registered as an input.\n" +
                "\tUse registerInput(path) in your GradmIntegrationPlugin.onApply() function."
        }
        return input
    }

    override fun outputFile(path: String): Path {
        val file = output(path)
        file.parent.createDirectories()
        if (file.notExists()) file.createFile()
        return file
    }

    override fun outputDirectory(path: String): Path = output(path).createDirectories()

    fun generate(): Unit = with(integration) { onGenerate() }

    fun refresh(): Unit = with(integration) { onRefresh() }

    private fun output(path: String): Path {
        val output = integrationDirectory.resolve(path)
        require(output in outputPaths) {
            "[$path] is not registered as an output.\n" +
                "\tUse registerOutput(path) in your GradmIntegrationPlugin.onApply() function."
        }
        return output
    }
}
