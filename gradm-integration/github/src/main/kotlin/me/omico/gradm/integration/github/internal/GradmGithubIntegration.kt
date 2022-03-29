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
package me.omico.gradm.integration.github.internal

import me.omico.gradm.internal.YamlArray
import me.omico.gradm.internal.YamlDocument
import me.omico.gradm.internal.YamlObject
import me.omico.gradm.internal.config.MutableFlatVersions
import me.omico.gradm.internal.find
import me.omico.gradm.internal.require

internal typealias Github = YamlObject
internal typealias GithubVersions = YamlArray
internal typealias GithubVersion = YamlObject

internal val YamlDocument.github: Github?
    get() = find("github")

internal val Github.versions: GithubVersions
    get() = require("versions")

internal val GithubVersion.repository: String
    get() = require("repository")

internal val GithubVersion.alias: String?
    get() = find("alias")

internal val GithubVersion.regex: String?
    get() = find("regex")

internal val GithubVersion.group: Int
    get() = find("group", 1)

internal data class GithubVersionConfiguration(
    val repository: String,
    val alias: String,
    val regex: Regex,
    val group: Int,
)

internal fun GithubVersionConfiguration(configuration: GithubVersion): GithubVersionConfiguration =
    GithubVersionConfiguration(
        repository = configuration.repository,
        alias = configuration.alias ?: configuration.repository.substringAfter("/"),
        regex = configuration.regex?.toRegex() ?: defaultSemverRegex,
        group = configuration.group,
    )

internal fun parseGithubIntegration(document: YamlDocument, versions: MutableFlatVersions) =
    document.github?.versions
        ?.map(::GithubVersionConfiguration)
        ?.forEach { configuration ->
            val tag = configuration.latestReleaseTag
            val result = configuration.regex.matchEntire(tag)
                ?: return@forEach println("[${configuration.repository}]: Unable to match regex ${configuration.regex} with tag $tag")
            val key = "versions.${configuration.alias}"
            when {
                versions.contains(key) -> println("[${configuration.repository}]: Duplicate with $key, skipping.")
                else -> runCatching { result.groupValues[configuration.group] }
                    .fold(
                        onSuccess = { versions[key] = it },
                        onFailure = { println("[${configuration.repository}]: Unable to parse version from regex ${configuration.regex} with tag $tag.") },
                    )
            }
        }

private val GithubVersionConfiguration.latestReleaseTag
    get() = latestReleaseTag(repository)

private val defaultSemverRegex =
    "(^(0|[1-9]\\d*)\\.(0|[1-9]\\d*)\\.(0|[1-9]\\d*)(?:-((?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*)(?:\\.(?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*))*))?(?:\\+([0-9a-zA-Z-]+(?:\\.[0-9a-zA-Z-]+)*))?\$)".toRegex()
