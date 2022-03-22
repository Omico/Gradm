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
package me.omico.gradm.internal.config

import me.omico.gradm.internal.YamlObject
import me.omico.gradm.internal.find
import me.omico.gradm.internal.require
import java.net.URL
import java.nio.file.Path

data class Library(
    val module: String,
    val version: String?,
    internal val alias: String?,
) {
    val group: String by lazy { module.split(':').first() }
    val artifact: String by lazy { module.split(':').last() }
}

fun Library.alias(): String = alias ?: artifact.replace("-", ".")

internal fun Library.metadataUrl(repositoryUrl: String): URL =
    URL("$repositoryUrl/${group.replace(".", "/")}/$artifact/maven-metadata.xml")

internal fun Library.metadataLocalPath(metadataRootDir: Path): Path =
    metadataRootDir.resolve(group).resolve(artifact).resolve("maven-metadata.xml")

internal fun Library(library: YamlObject): Library =
    Library(
        module = when {
            library.containsKey("module") -> library.require("module")
            else -> "${library.require<String>("group")}:${library.require<String>("artifact")}"
        },
        alias = library.find("alias"),
        version = library.find("version"),
    )

private val fixedVersionRegex = Regex("(\\\$\\{(.*)})")

internal fun fixedVersion(version: String?, versions: Versions): String? =
    version
        ?.let { fixedVersionRegex.matchEntire(it) }
        ?.let { it.groupValues[2] }
        ?.let { versions[it] }
        ?: version
