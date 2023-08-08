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

import me.omico.gradm.internal.YamlDocument
import me.omico.gradm.internal.config.MutableFlatVersions
import me.omico.gradm.path.GradmProjectPaths
import java.nio.file.Path

interface GradmIntegrationHolder {
    /**
     * The attributes of this integration.
     */
    val attributes: Map<String, Any>

    /**
     * Resolved configuration of this integration.
     */
    val integrationConfiguration: YamlDocument

    /**
     * Resolved instance of GradmProjectPaths.
     */
    val gradmProjectPaths: GradmProjectPaths

    /**
     * The versions that will be used for dependencies.
     * This is a mutable object, so it can be modified by the integration.
     * Which also means integrations are applied sequentially.
     */
    val versions: MutableFlatVersions

    /**
     * Get input file/directory that is registered by [GradmIntegrationOwner.registerInput].
     */
    fun input(path: String): Path

    /**
     * Get output file that is registered by [GradmIntegrationOwner.registerOutput].
     */
    fun outputFile(path: String): Path

    /**
     * Get output directory that is registered by [GradmIntegrationOwner.registerOutput].
     */
    fun outputDirectory(path: String): Path
}
