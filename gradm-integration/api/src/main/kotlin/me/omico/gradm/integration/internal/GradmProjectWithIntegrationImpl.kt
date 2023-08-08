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

import me.omico.gradm.GradmExtension
import me.omico.gradm.integration.GradmIntegrationExtension
import me.omico.gradm.integration.GradmIntegrationOwner
import me.omico.gradm.integration.GradmIntegrationPlugin
import me.omico.gradm.integration.GradmIntegrationsExtension
import me.omico.gradm.integration.GradmProjectWithIntegration
import me.omico.gradm.service.GradmBuildService
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.getByType
import java.nio.file.Path
import kotlin.io.path.absolutePathString
import kotlin.reflect.KClass

internal class GradmProjectWithIntegrationImpl<Extension : GradmIntegrationExtension>(
    private val plugin: GradmIntegrationPlugin<Extension>,
    private val project: Project,
) : GradmProjectWithIntegration<Extension>,
    Project by project {
    private val id: String = plugin.id

    private val integrationDirectory: Path =
        project.layout.buildDirectory.dir("gradm/integration/$id").get().asFile.toPath()

    private val owner: GradmIntegrationOwner = requireOwner()

    override val gradmExtension: GradmExtension = extensions.getByType()

    override val gradmIntegrationsExtension: GradmIntegrationsExtension = gradmExtension.extensions.getByType()

    override fun <ExtensionImpl : Extension> createExtension(
        extension: KClass<Extension>,
        extensionImpl: KClass<ExtensionImpl>,
    ): Extension =
        gradmIntegrationsExtension.extensions.create(
            publicType = extension,
            name = id,
            instanceType = extensionImpl,
        )

    override fun registerExtension(extension: Extension): Unit = owner.register(plugin, extension)

    override fun registerInput(path: String): Unit = owner.registerInput(id, integrationDirectory.resolve(path))

    override fun registerOutput(path: String) {
        val output = integrationDirectory.resolve(path)
        require(integrationDirectory.absolutePathString() in output.absolutePathString()) {
            "Output must be located in integration's directory."
        }
        owner.registerOutput(id, output)
    }

    private fun requireOwner(): GradmIntegrationOwner {
        val registration = gradle.sharedServices.registrations
            .firstOrNull { it.name == GradmBuildService.NAME } ?: error("${GradmBuildService.NAME} not found.")
        return registration.service.get() as GradmIntegrationOwner
    }
}
