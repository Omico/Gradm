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

import org.gradle.api.Action
import org.gradle.api.plugins.ExtensionAware

// TODO Remove deprecated methods in Gradm 5.0.0.
interface GradmIntegrationsExtension : ExtensionAware {
    @Deprecated(
        message = "Use `register` instead. This method will be removed in Gradm 5.0.0.",
        replaceWith = ReplaceWith("register(id)"),
        level = DeprecationLevel.WARNING,
    )
    fun apply(id: String)

    @Deprecated(
        message = "Use `register` instead. This method will be removed in Gradm 5.0.0.",
        replaceWith = ReplaceWith("register(id,configure)"),
        level = DeprecationLevel.WARNING,
    )
    fun apply(id: String, configure: Action<GradmIntegrationExtension>)

    fun register(id: String)
    fun register(id: String, configure: Action<GradmIntegrationExtension>)
}
