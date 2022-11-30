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
package me.omico.gradm.integration

import me.omico.gradm.debug
import me.omico.gradm.internal.config.MutableFlatVersions
import me.omico.gradm.path.GradmProjectPaths
import java.util.ServiceLoader

fun GradmProjectPaths.applyGradmIntegrations(versions: MutableFlatVersions) =
    GradmIntegrationConfigurations.activatedIntegrations.forEach { (integration, configuration) ->
        integration.applyVersions(this, configuration, versions)
    }

object GradmIntegrationConfigurations {

    private val integrations by lazy(::discoverGradmIntegrations)

    internal val activatedIntegrations = hashMapOf<GradmIntegration, GradmIntegrationConfiguration>()

    fun save(configuration: GradmIntegrationConfiguration) {
        val integration = integrations[configuration.id]
        requireNotNull(integration) { "Integration [${configuration.id}] cannot be applied because it is not found." }
        if (!configuration.enabled) {
            debug { "Integration [${configuration.id}] is manually disabled." }
            return
        }
        activatedIntegrations[integration] = configuration
    }
}

private fun discoverGradmIntegrations(): Map<String, GradmIntegration> =
    ServiceLoader.load(GradmIntegration::class.java).associateBy { it.id }
