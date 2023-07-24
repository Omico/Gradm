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
    get() = find<YamlObject>("repositories", emptyMap())
        .map { (id, attributes) -> buildInRepositories.find { it.id == id } ?: repository(id, attributes) }

data class Repository(
    val id: String,
    val url: String,
    val noUpdates: Boolean,
    val buildIn: Boolean,
)

val gradleBuildInRepositories: List<Repository> =
    listOf(
        buildInRepository(id = "gradlePluginPortal", url = "https://plugins.gradle.org/m2"),
        buildInRepository(id = "mavenCentral", url = "https://repo1.maven.org/maven2"),
        buildInRepository(id = "mavenLocal", noUpdates = true),
        buildInRepository(id = "google", url = "https://maven.google.com"),
    )

val buildInRepositories: List<Repository> =
    gradleBuildInRepositories + listOf(
        buildInRepository(id = "noUpdates", noUpdates = true),
    )

internal fun String.fixedUrl(): String =
    when {
        endsWith("/") -> removeSuffix("/")
        else -> this
    }

internal fun List<Repository>.requireRepository(id: String): Repository =
    requireNotNull(find { it.id == id }) { "Repository $id not found." }

@Suppress("UNCHECKED_CAST")
private fun repository(id: String, attributes: Any?): Repository =
    run {
        attributes as? YamlObject ?: error("Repository $id attributes is missing.")
        Repository(
            id = id,
            url = attributes.find<String>("url")?.fixedUrl() ?: "",
            noUpdates = attributes.find("noUpdates", false),
            buildIn = false,
        )
    }

private fun buildInRepository(id: String, url: String = "", noUpdates: Boolean = false): Repository =
    Repository(
        id = id,
        url = url,
        noUpdates = noUpdates,
        buildIn = true,
    )
