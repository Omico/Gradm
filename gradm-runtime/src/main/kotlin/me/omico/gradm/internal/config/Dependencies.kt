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

import me.omico.gradm.internal.YamlArray
import me.omico.gradm.internal.YamlDocument
import me.omico.gradm.internal.YamlObject
import me.omico.gradm.internal.find
import me.omico.gradm.internal.require

val YamlDocument.dependencies: List<Dependency>
    get() = find<YamlArray>("dependencies", emptyList())
        .map { Dependency(it, versions.toFlatVersions()) }

data class Dependency(
    val name: String,
    val repository: String,
    val libraries: List<Library>,
)

internal fun Dependency.repositoryUrl(repositories: List<Repository>): String =
    repositories.find { it.id == repository }?.url ?: error("Repository not found.")

internal typealias DependencyObject = YamlObject

internal fun Dependency(dependencyObject: DependencyObject): Dependency =
    Dependency(
        name = dependencyObject.require("name"),
        repository = dependencyObject.require("repository"),
        libraries = dependencyObject.find<YamlArray>("libraries", emptyList()).map(::Library).sortedBy { it.module },
    )

private fun Dependency(dependencyObject: DependencyObject, versions: FlatVersions): Dependency =
    Dependency(dependencyObject).let { dependency ->
        dependency.copy(
            libraries = dependency.libraries.map { library ->
                library.copy(version = versions.resolveVariable(library.version))
            },
        )
    }
