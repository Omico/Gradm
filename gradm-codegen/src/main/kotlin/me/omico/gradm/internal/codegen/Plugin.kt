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
package me.omico.gradm.internal.codegen

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeSpec
import me.omico.gradm.internal.YamlDocument
import me.omico.gradm.internal.config.plugins
import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import java.nio.file.Path

fun generatePluginSourceFiles(
    generatedSourcesDirectory: Path,
    document: YamlDocument,
) {
    FileSpec.builder("me.omico.gradm.generated", "GradmPlugin")
        .addSuppressWarningTypes()
        .addGradmComment()
        .apply {
            TypeSpec.classBuilder("GradmPlugin")
                .addSuperinterface(Plugin::class.parameterizedBy(Settings::class))
                .addOverrideApplyFunction(document)
                .build()
                .also(::addType)
        }
        .build()
        .writeTo(generatedSourcesDirectory)
}

private fun TypeSpec.Builder.addOverrideApplyFunction(document: YamlDocument): TypeSpec.Builder =
    apply {
        FunSpec.builder("apply")
            .addModifiers(KModifier.OVERRIDE)
            .addParameter("target", Settings::class)
            .apply {
                document.plugins.forEach { plugin ->
                    addStatement("target.pluginManagement.plugins.id(\"${plugin.id}\").version(\"${plugin.version}\")")
                }
            }
            .build()
            .also(::addFunction)
    }
