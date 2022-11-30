/*
 * Copyright 2022 Omico
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
import org.gradle.api.Project
import org.gradle.api.file.RegularFileProperty
import org.gradle.plugin.devel.GradlePluginDevelopmentExtension
import javax.inject.Inject

abstract class GradmExtensionImpl @Inject constructor(
    private val project: Project,
    pluginDevelopmentExtension: GradlePluginDevelopmentExtension,
) : GradmExtension {

    private val pluginDeclaration = pluginDevelopmentExtension.plugins.create("gradm") {
        id = "me.omico.gradm.generated"
        implementationClass = "me.omico.gradm.generated.GradmPlugin"
    }

    private val configFileProperty: RegularFileProperty = project.objects.fileProperty()

    override var pluginId: String
        get() = pluginDeclaration.id
        set(value) {
            pluginDeclaration.id = value
        }

    override var configFilePath: String
        get() = configFileProperty.asFile.getOrElse(project.file("gradm.yml")).absolutePath
        set(value) {
            val file = project.file(value)
            require(file.exists()) { "The path you assign for the Gradm config file does not exist." }
            require(file.isFile) { "Gradm config file must be a file." }
            configFileProperty.set(file)
        }

    override var debug: Boolean
        get() = GradmConfiguration.debug
        set(value) {
            GradmConfiguration.debug = value
        }
}
