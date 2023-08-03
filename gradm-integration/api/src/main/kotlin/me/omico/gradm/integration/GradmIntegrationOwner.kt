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

import java.nio.file.Path

interface GradmIntegrationOwner {
    /**
     * Register a [GradmIntegration] to this owner.
     * @param integration the integration to register
     * @param extension the extension of the integration
     * @throws IllegalArgumentException if the integration is already registered
     *
     * Internal use only. Do not call this method directly.
     * Use [GradmProjectWithIntegration.registerExtension] instead.
     */
    fun register(integration: GradmIntegration, extension: GradmIntegrationExtension)

    /**
     * Register an input path to this owner.
     * @param id the id of the input path
     * @param path the input path to register
     *
     * Internal use only. Do not call this method directly.
     * Use [GradmProjectWithIntegration.registerInput] instead.
     */
    fun registerInput(id: String, path: Path)

    /**
     * Register an output path to this owner.
     * @param id the id of the output path
     * @param path the output path to register
     *
     * Internal use only. Do not call this method directly.
     * Use [GradmProjectWithIntegration.registerOutput] instead.
     */
    fun registerOutput(id: String, path: Path)
}
