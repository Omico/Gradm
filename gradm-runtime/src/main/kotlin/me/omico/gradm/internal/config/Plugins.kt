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

import me.omico.gradm.internal.YamlDocument
import me.omico.gradm.internal.YamlObject
import me.omico.gradm.internal.find

val YamlDocument.plugins: List<Plugin>
    @Suppress("UNCHECKED_CAST")
    get() = run {
        val repositories = repositories
        val versions = versions.toFlatVersions()
        find<YamlObject>("plugins", emptyMap())
            .flatMap { (repository, plugin) ->
                val repositoryUrl = requireNotNull(repositories.find { it.id == repository }?.url) {
                    "Repository $repository not found."
                }
                (plugin as YamlObject).map { (id, version) ->
                    version as String
                    Plugin(
                        repository = repositoryUrl,
                        id = id,
                        version = version.let(versions::resolveVariable),
                    )
                }
            }
    }

data class Plugin(
    val repository: String,
    val id: String,
    val version: String?,
) {
    val group: String by lazy { id }
    val artifact: String by lazy { "$id.gradle.plugin" }
}

internal fun Plugin.toDependency(): Dependency =
    Dependency(
        repository = repository,
        group = group,
        artifact = artifact,
        alias = "",
        version = version,
    )
