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
package me.omico.gradm

import org.gradle.api.plugins.ExtensionAware

interface GradmExtension : ExtensionAware {
    /**
     * The name of the project to which this plugin is applied.
     */
    val projectName: String

    /**
     * The plugin id of the Gradm plugin.
     *
     * The default value is `me.omico.gradm.generated`.
     */
    var pluginId: String

    /**
     * The Gradm configuration file path.
     *
     * The default value is `./gradm.yml`.
     */
    var configurationFilePath: String

    /**
     * Enable debug mode for Gradm.
     *
     * The default value is `false`.
     */
    var debug: Boolean
}
