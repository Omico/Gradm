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

import me.omico.gradm.GradmExtension
import org.gradle.api.Project
import kotlin.reflect.KClass

interface GradmProjectWithIntegration<Extension : GradmIntegrationExtension> : Project {
    /**
     * The [GradmExtension] of this project.
     */
    val gradmExtension: GradmExtension

    /**
     * The [GradmIntegrationsExtension] of this project.
     */
    val gradmIntegrationsExtension: GradmIntegrationsExtension

    /**
     * Create an extension for this integration.
     * @param extension The extension class.
     * @param extensionImpl The extension implementation class.
     * @return The [extension] instance.
     */
    fun <ExtensionImpl : Extension> createExtension(
        extension: KClass<Extension>,
        extensionImpl: KClass<ExtensionImpl>,
    ): Extension

    /**
     * Register an extension for this integration.
     * @param extension The extension instance.
     */
    fun registerExtension(extension: Extension)

    /**
     * Register an input file/directory for this integration.
     * @param path The path of the input file/directory.
     */
    fun registerInput(path: String)

    /**
     * Register an output file/directory for this integration.
     * @param path The path of the output file/directory.
     */
    fun registerOutput(path: String)
}

inline fun <
    reified Extension : GradmIntegrationExtension,
    reified ExtensionImpl : Extension,
    > GradmProjectWithIntegration<Extension>.createExtension(): Extension =
    createExtension(
        extension = Extension::class,
        extensionImpl = ExtensionImpl::class,
    )
