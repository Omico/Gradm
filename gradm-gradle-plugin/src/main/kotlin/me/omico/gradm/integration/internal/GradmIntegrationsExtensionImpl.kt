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
package me.omico.gradm.integration.internal

import me.omico.gradm.integration.GradmIntegrationConfiguration
import me.omico.gradm.integration.GradmIntegrationConfigurations
import me.omico.gradm.integration.GradmIntegrationExtension
import me.omico.gradm.integration.GradmIntegrationsExtension
import org.gradle.api.Action

internal abstract class GradmIntegrationsExtensionImpl : GradmIntegrationsExtension {
    @Suppress("OVERRIDE_DEPRECATION")
    override fun apply(id: String) = register(id)

    @Suppress("OVERRIDE_DEPRECATION")
    override fun apply(id: String, configure: Action<GradmIntegrationExtension>) = register(id, configure)

    override fun register(id: String) = register(id) {}
    override fun register(id: String, configure: Action<GradmIntegrationExtension>) =
        GradmIntegrationExtensionImpl(id)
            .apply(configure::execute)
            .toConfiguration()
            .let(GradmIntegrationConfigurations::save)
}

private fun GradmIntegrationExtensionImpl.toConfiguration() =
    GradmIntegrationConfiguration(
        id = id,
        enabled = enabled,
        configFilePath = configFilePath,
    )
