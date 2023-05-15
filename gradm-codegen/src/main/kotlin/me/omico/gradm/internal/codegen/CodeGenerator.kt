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

import me.omico.elucidator.KtFileScope
import me.omico.elucidator.addAnnotation
import me.omico.elucidator.addFileComment
import me.omico.elucidator.addMember
import me.omico.gradm.VersionsMeta
import me.omico.gradm.internal.YamlDocument
import me.omico.gradm.internal.config.FlatVersions
import me.omico.gradm.internal.config.matchesVariableVersion
import me.omico.gradm.internal.config.toFlatVersions
import me.omico.gradm.internal.config.versions
import me.omico.gradm.path.GradmProjectPaths
import me.omico.gradm.utility.clearDirectory
import java.nio.file.Path
import java.util.Locale

fun generateGradmGeneratedSources(
    gradmProjectPaths: GradmProjectPaths,
    gradmConfigDocument: YamlDocument,
    versionsMeta: VersionsMeta,
    generatedSourcesDirectory: Path,
) {
    generatedSourcesDirectory.clearDirectory()
    CodeGenerator(
        gradmProjectPaths = gradmProjectPaths,
        gradmConfigDocument = gradmConfigDocument,
        versionsMeta = versionsMeta,
        generatedSourcesDirectory = generatedSourcesDirectory,
    ).run {
        generateVersionsSourceFile()
        generatePluginSourceFile()
        generateDependenciesSourceFiles()
        generateSelfSourceFile()
    }
}

internal class CodeGenerator(
    val gradmProjectPaths: GradmProjectPaths,
    val gradmConfigDocument: YamlDocument,
    val versionsMeta: VersionsMeta,
    val generatedSourcesDirectory: Path,
) {
    val dependencies: CodegenDependencies = gradmConfigDocument.createCodegenDependencies(versionsMeta)
    val flatVersions: FlatVersions = gradmConfigDocument.versions.toFlatVersions()
}

internal val defaultSuppressWarningTypes: Array<String> =
    arrayOf(
        "MemberVisibilityCanBePrivate",
        "RedundantVisibilityModifier",
        "unused",
    )

internal fun KtFileScope.addSuppressWarningTypes(vararg types: String = defaultSuppressWarningTypes): Unit =
    addAnnotation<Suppress> { addMember("%S,".repeat(types.count()).trimEnd(','), *types) }

internal fun KtFileScope.addGradmComment(): Unit =
    addFileComment(
        """
        |
        |Generated by Gradm, will be overwritten by every dependencies update, do not edit!!!
        |
        """.trimMargin(),
    )

internal fun String.capitalize(): String =
    replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

internal fun String.camelCase(): String =
    split("-", "_")
        .mapIndexed { index, s -> if (index == 0) s else s.capitalize() }
        .joinToString("")

internal fun VersionsMeta.resolveVariable(module: String, version: String?): String? =
    when {
        version == null -> null
        matchesVariableVersion(version) -> this[module]
        else -> version
    }
