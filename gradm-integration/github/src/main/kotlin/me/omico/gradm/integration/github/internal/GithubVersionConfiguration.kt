/*
 * Copyright 2023 Omico
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

import me.omico.gradm.debug
import me.omico.gradm.info
import me.omico.gradm.internal.config.FlatVersions
import me.omico.gradm.internal.find

internal data class GithubVersionConfiguration(
    val repository: String,
    val alias: String,
    val regex: Regex,
    val group: Int,
    val matchType: MatchType,
) {
    enum class MatchType {
        EXACT,
        PARTIAL,
    }
}

internal fun GithubVersionConfiguration(version: GithubVersion): GithubVersionConfiguration =
    GithubVersionConfiguration(
        repository = version.repository,
        alias = version.alias ?: version.repository.substringAfter("/"),
        regex = version.regex?.toRegex() ?: defaultSemverRegex,
        group = version.group,
        matchType = version.findMatchType(),
    )

internal fun GithubVersionConfiguration.versionPair(versions: FlatVersions): Pair<String, String>? {
    debug { "Fetching latest release tag for [$repository]" }
    val tag = fetchLatestReleaseTag(repository)
    val result = when (matchType) {
        GithubVersionConfiguration.MatchType.EXACT -> regex.matchEntire(tag)
        GithubVersionConfiguration.MatchType.PARTIAL -> regex.find(tag)
    }
    if (result == null) {
        info { "For [$repository], unable to match regex \"${regex}\" with tag \"$tag\"" }
        return null
    }
    val key = "versions.$alias"
    if (key in versions) {
        info { "Duplicate with $key in [$repository], skipping." }
        return null
    }
    runCatching { result.groupValues[group] }
        .onSuccess { return key to it }
        .onFailure {
            info { "Unable to parse the version for [$repository] via regex \"${regex}\" with tag \"$tag\" in group values ${result.groupValues}." }
        }
    return null
}

private fun GithubVersion.findMatchType(): GithubVersionConfiguration.MatchType =
    find("matchType", "exact")
        .let(String::uppercase)
        .let(GithubVersionConfiguration.MatchType::valueOf)

private val defaultSemverRegex: Regex =
    "(^(0|[1-9]\\d*)\\.(0|[1-9]\\d*)\\.(0|[1-9]\\d*)(?:-((?:0|[1-9]\\d*|\\d*[a-zA-Z-][\\da-zA-Z-]*)(?:\\.(?:0|[1-9]\\d*|\\d*[a-zA-Z-][\\da-zA-Z-]*))*))?(?:\\+([\\da-zA-Z-]+(?:\\.[\\da-zA-Z-]+)*))?\$)".toRegex()
