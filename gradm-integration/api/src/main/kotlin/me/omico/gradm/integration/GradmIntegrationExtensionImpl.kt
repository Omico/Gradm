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

/**
 * The implementation of [GradmIntegrationExtension].
 *
 * Recommend implementing this class instead of [GradmIntegrationExtension].
 */
abstract class GradmIntegrationExtensionImpl(
    /**
     * The id of this integration.
     */
    override val id: String,
) : GradmIntegrationExtension {
    /**
     * The attributes of this integration.
     */
    final override val attributes: MutableMap<String, Any> = mutableMapOf()

    /**
     * Whether this integration is enabled.
     *
     * Defaults to `true`.
     */
    override var enabled: Boolean by attributes.withDefault { true }

    /**
     * The path of the configuration file for this integration.
     *
     * Defaults to `gradm.integration.<id>.yml`.
     */
    override var configurationFilePath: String by attributes.withDefault { "gradm.integration.$id.yml" }
}
