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
package me.omico.gradm.typesafe.internal

import me.omico.gradm.GradmGeneratedPluginType
import me.omico.gradm.createGradmGeneratedPluginDeclaration
import me.omico.gradm.typesafe.GradmTypesafeExtension
import org.gradle.plugin.devel.GradlePluginDevelopmentExtension
import org.gradle.plugin.devel.PluginDeclaration
import javax.inject.Inject

internal abstract class GradmTypesafeExtensionImpl @Inject constructor(
    pluginDevelopmentExtension: GradlePluginDevelopmentExtension,
) : GradmTypesafeExtension {
    private val pluginDeclaration: PluginDeclaration by lazy {
        pluginDevelopmentExtension.createGradmGeneratedPluginDeclaration(GradmGeneratedPluginType.Typesafe)
    }
    override var pluginId: String by pluginDeclaration::id
}
