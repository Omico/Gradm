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
package me.omico.gradm.integration

import me.omico.gradm.debug
import me.omico.gradm.integration.internal.GradmIntegrationConfiguration
import me.omico.gradm.integration.internal.GradmIntegrationHolderImpl
import me.omico.gradm.internal.config.MutableFlatVersions
import me.omico.gradm.path.GradmProjectPaths
import java.nio.file.Path

object GradmIntegrationManager : GradmIntegrationOwner {
    private val integrationConfigurations: MutableMap<String, GradmIntegrationConfiguration> = mutableMapOf()
    private val integrationInputPaths: MutableMap<String, MutableSet<Path>> = mutableMapOf()
    private val integrationOutputPaths: MutableMap<String, MutableSet<Path>> = mutableMapOf()

    val integrationConfigurationFilePaths: List<String>
        get() = integrationConfigurations.values.map(GradmIntegrationConfiguration::configurationFilePath)

    val inputPaths: Set<Path>
        get() = integrationInputPaths.values.flatten().toSet()

    val outputPaths: Set<Path>
        get() = integrationOutputPaths.values.flatten().toSet()

    override fun register(integration: GradmIntegration, extension: GradmIntegrationExtension) {
        val id = integration.id
        if (!extension.enabled) {
            debug { "Integration [$id] is manually disabled." }
            return
        }
        val configuration = GradmIntegrationConfiguration(integration, extension)
        integrationConfigurations[id] = configuration
    }

    override fun registerInput(id: String, path: Path) {
        integrationInputPaths.fromId(id).add(path)
    }

    override fun registerOutput(id: String, path: Path) {
        integrationOutputPaths.fromId(id).add(path)
    }

    fun generate(gradmProjectPaths: GradmProjectPaths, versions: MutableFlatVersions): Unit =
        collectIntegrationHolders(gradmProjectPaths, versions).forEach(GradmIntegrationHolderImpl::generate)

    fun refresh(gradmProjectPaths: GradmProjectPaths, versions: MutableFlatVersions): Unit =
        collectIntegrationHolders(gradmProjectPaths, versions).forEach(GradmIntegrationHolderImpl::refresh)

    private fun collectIntegrationHolders(
        gradmProjectPaths: GradmProjectPaths,
        versions: MutableFlatVersions,
    ): List<GradmIntegrationHolderImpl> =
        integrationConfigurations.map { (id, configuration) ->
            GradmIntegrationHolderImpl(
                id = id,
                attributes = configuration.attributes,
                configurationFilePath = configuration.configurationFilePath,
                integration = configuration.integration,
                inputPaths = integrationInputPaths.fromId(id),
                outputPaths = integrationOutputPaths.fromId(id),
                gradmProjectPaths = gradmProjectPaths,
                versions = versions,
            )
        }

    private fun MutableMap<String, MutableSet<Path>>.fromId(id: String): MutableSet<Path> = getOrPut(id, ::mutableSetOf)
}
