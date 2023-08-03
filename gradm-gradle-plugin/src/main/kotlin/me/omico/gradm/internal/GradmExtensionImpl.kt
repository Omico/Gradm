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
package me.omico.gradm.internal

import me.omico.gradm.GradmConfiguration
import me.omico.gradm.GradmExtension
import me.omico.gradm.GradmGeneratedPluginType
import me.omico.gradm.createGradmGeneratedPluginDeclaration
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType
import org.gradle.plugin.devel.GradlePluginDevelopmentExtension
import org.gradle.plugin.devel.PluginDeclaration
import javax.inject.Inject

internal abstract class GradmExtensionImpl @Inject constructor(
    private val project: Project,
) : GradmExtension {
    private val pluginDevelopmentExtension: GradlePluginDevelopmentExtension = project.extensions.getByType()

    private val pluginDeclaration: PluginDeclaration =
        pluginDevelopmentExtension.createGradmGeneratedPluginDeclaration(GradmGeneratedPluginType.General)

    override val projectName: String
        get() = project.name

    override var pluginId: String by pluginDeclaration::id

    override var configurationFilePath: String = "gradm.yml"
        set(value) {
            val file = project.file(value)
            require(file.exists()) { "Gradm configuration file does not exist in ${file.absolutePath}." }
            require(file.isFile) { "Gradm configuration file must be a file." }
            field = value
        }

    override var debug: Boolean by GradmConfiguration::debug
}
