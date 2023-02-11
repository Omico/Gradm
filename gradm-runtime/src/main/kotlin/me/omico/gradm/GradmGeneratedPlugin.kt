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
package me.omico.gradm

import org.gradle.plugin.devel.GradlePluginDevelopmentExtension
import org.gradle.plugin.devel.PluginDeclaration

sealed class GradmGeneratedPluginType(
    val name: String,
    val pluginId: String,
    val packageName: String,
    val className: String,
) {
    val implementationClass: String by lazy { "$packageName.$className" }

    object General : GradmGeneratedPluginType(
        name = "gradmGenerated",
        pluginId = "me.omico.gradm.generated",
        packageName = "me.omico.gradm.generated",
        className = "GradmPlugin",
    )
}

fun GradlePluginDevelopmentExtension.createGradmGeneratedPluginDeclaration(type: GradmGeneratedPluginType): PluginDeclaration =
    plugins.create(type.name) { declaration ->
        declaration.id = type.pluginId
        declaration.implementationClass = type.implementationClass
    }
