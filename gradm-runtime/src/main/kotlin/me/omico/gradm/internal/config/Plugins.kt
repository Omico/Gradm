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
            .flatMap { (repositoryId, plugin) ->
                val repository = repositories.requireRepository(repositoryId)
                (plugin as YamlObject).map { (id, version) ->
                    version as String
                    Plugin(
                        repository = repository.url,
                        noUpdates = repository.noUpdates,
                        id = id,
                        version = version.let(versions::resolveVariable),
                    )
                }
            }
    }

data class Plugin(
    val repository: String,
    val noUpdates: Boolean,
    val id: String,
    val version: String?,
) {
    val group: String by lazy { id }
    val artifact: String by lazy { "$id.gradle.plugin" }
    val module: String by lazy { "$group:$artifact" }
}

internal fun Plugin.toDependency(): Dependency =
    Dependency(
        repository = repository,
        noUpdates = noUpdates,
        noSpecificVersion = false,
        bom = false,
        group = group,
        artifact = artifact,
        alias = id,
        version = version,
    )
