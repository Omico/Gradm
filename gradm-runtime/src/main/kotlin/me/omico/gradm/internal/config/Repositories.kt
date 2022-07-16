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
import me.omico.gradm.internal.require

val YamlDocument.repositories: List<Repository>
    @Suppress("UNCHECKED_CAST")
    get() = ArrayList<Repository>().apply {
        add(noUpdatesRepository)
        find<YamlObject>("repositories", emptyMap())
            .map { (id, attributes) ->
                attributes as YamlObject
                val noUpdates = attributes.find("noUpdates", false)
                val url = when {
                    noUpdates -> ""
                    else -> attributes.require<String>("url").fixedUrl()
                }
                Repository(
                    id = id,
                    noUpdates = noUpdates,
                    url = url,
                )
            }
            .let(::addAll)
    }

data class Repository(
    val id: String,
    val noUpdates: Boolean,
    val url: String,
)

internal val noUpdatesRepository: Repository =
    Repository(
        id = "noUpdates",
        noUpdates = true,
        url = "",
    )

internal fun String.fixedUrl(): String =
    when {
        endsWith("/") -> removeSuffix("/")
        else -> this
    }

internal fun List<Repository>.requireRepository(id: String): Repository =
    requireNotNull(find { it.id == id }) { "Repository $id not found." }
