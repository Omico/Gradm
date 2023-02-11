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
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.asClassName
import me.omico.gradm.path.GradmProjectPaths
import me.omico.gradm.path.generatedJar
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import java.nio.file.Path
import kotlin.io.path.invariantSeparatorsPathString

fun generateSelfSourceFile(
    gradmProjectPaths: GradmProjectPaths,
    generatedSourcesDirectory: Path,
) {
    FileSpec.builder("", "Self")
        .addSuppressWarningTypes()
        .addGradmComment()
        .apply {
            PropertySpec
                .builder("gradmGeneratedJar", ConfigurableFileCollection::class)
                .receiver(Project::class)
                .getter(
                    FunSpec.getterBuilder()
                        .addStatement(
                            "return files(\"${gradmProjectPaths.generatedJar.invariantSeparatorsPathString}\")",
                            ConfigurableFileCollection::class.asClassName(),
                        )
                        .build(),
                )
                .build()
                .also(::addProperty)
        }
        .build()
        .writeTo(generatedSourcesDirectory)
}
