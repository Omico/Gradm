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

import me.omico.elucidator.addProperty
import me.omico.elucidator.getter
import me.omico.elucidator.ktFile
import me.omico.elucidator.receiver
import me.omico.elucidator.writeTo
import me.omico.gradm.path.generatedJar
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import kotlin.io.path.invariantSeparatorsPathString

internal fun CodeGenerator.generateSelfSourceFile() =
    ktFile(fileName = "Self") {
        addSuppressWarningTypes()
        addGradmComment()
        addProperty<ConfigurableFileCollection>("gradmGeneratedJar") {
            receiver<Project>()
            getter("files(\"${gradmProjectPaths.generatedJar.invariantSeparatorsPathString}\")")
        }
        writeTo(generatedSourcesDirectory)
    }
