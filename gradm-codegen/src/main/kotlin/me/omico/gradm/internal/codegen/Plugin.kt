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
import me.omico.gradm.GradmGeneratedPluginType
import me.omico.gradm.internal.YamlDocument
import me.omico.gradm.internal.config.plugins
import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import java.nio.file.Path

fun generatePluginSourceFile(generatedSourcesDirectory: Path, document: YamlDocument) =
    generatePluginSourceFile<Settings>(
        generatedSourcesDirectory = generatedSourcesDirectory,
        type = GradmGeneratedPluginType.General,
        overrideApplyFunctionBuilder = {
            beginControlFlow("target.pluginManagement.plugins")
            document.plugins
                .sortedBy { plugin -> plugin.id }
                .forEach { plugin -> addStatement("id(\"${plugin.id}\").version(\"${plugin.version}\").apply(false)") }
            endControlFlow()
        },
    )

private inline fun <reified T> generatePluginSourceFile(
    generatedSourcesDirectory: Path,
    type: GradmGeneratedPluginType,
    overrideApplyFunctionBuilder: FunSpec.Builder.() -> Unit = {},
) = FileSpec.builder(type.packageName, type.className)
    .addSuppressWarningTypes()
    .addGradmComment()
    .apply {
        TypeSpec.classBuilder(type.className)
            .addSuperinterface(Plugin::class.parameterizedBy(T::class))
            .addOverrideApplyFunction<T>(overrideApplyFunctionBuilder)
            .build()
            .also(::addType)
    }
    .build()
    .writeTo(generatedSourcesDirectory)

private inline fun <reified T> TypeSpec.Builder.addOverrideApplyFunction(
    overrideApplyFunctionBuilder: FunSpec.Builder.() -> Unit = {},
): TypeSpec.Builder =
    apply {
        FunSpec.builder("apply")
            .addModifiers(KModifier.OVERRIDE)
            .addParameter("target", T::class)
            .apply(overrideApplyFunctionBuilder)
            .build()
            .also(::addFunction)
    }
