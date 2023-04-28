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

val YamlDocument.repositories: List<Repository>
    @Suppress("UNCHECKED_CAST")
    get() = find<YamlObject>("repositories", emptyMap())
        .map { (id, attributes) ->
            val buildInRepository = buildInRepositories.find { it.id == id }
            if (buildInRepository != null) return@map buildInRepository
            Repository(
                id = id,
                attributes = attributes as YamlObject,
            )
        }

data class Repository(
    val id: String,
    val noUpdates: Boolean,
    val url: String,
)

val buildInRepositories: List<Repository> by lazy {
    listOf(
        Repository(
            id = "google",
            noUpdates = false,
            url = "https://maven.google.com",
        ),
        Repository(
            id = "mavenCentral",
            noUpdates = false,
            url = "https://repo1.maven.org/maven2",
        ),
        Repository(
            id = "gradlePluginPortal",
            noUpdates = false,
            url = "https://plugins.gradle.org/m2",
        ),
        Repository(
            id = "mavenLocal",
            noUpdates = true,
            url = "",
        ),
        Repository(
            id = "noUpdates",
            noUpdates = true,
            url = "",
        ),
    )
}

internal fun String.fixedUrl(): String =
    when {
        endsWith("/") -> removeSuffix("/")
        else -> this
    }

internal fun List<Repository>.requireRepository(id: String): Repository =
    requireNotNull(find { it.id == id }) { "Repository $id not found." }

private fun Repository(id: String, attributes: YamlObject): Repository =
    Repository(
        id = id,
        noUpdates = attributes.find("noUpdates", false),
        url = attributes.find<String>("url")?.fixedUrl() ?: "",
    )
