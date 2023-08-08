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
package me.omico.gradm.integration

import me.omico.gradm.GRADM_PLUGIN_ID
import me.omico.gradm.integration.internal.GradmProjectWithIntegrationImpl
import org.gradle.api.Plugin
import org.gradle.api.Project

abstract class GradmIntegrationPlugin<Extension : GradmIntegrationExtension> : Plugin<Project>, GradmIntegration {
    /**
     * This method is called when the plugin is applied.
     * @see [Plugin.apply]
     */
    open fun GradmProjectWithIntegration<Extension>.onApply() {}

    final override fun apply(target: Project): Unit =
        target.plugins.withId(GRADM_PLUGIN_ID) {
            GradmProjectWithIntegrationImpl(plugin = this, project = target).onApply()
        }
}
