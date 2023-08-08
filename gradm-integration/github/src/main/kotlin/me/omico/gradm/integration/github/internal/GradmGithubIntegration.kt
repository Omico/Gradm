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
package me.omico.gradm.integration.github.internal

import me.omico.gradm.VersionsMeta
import me.omico.gradm.asVersionsMeta
import me.omico.gradm.info
import me.omico.gradm.internal.YamlArray
import me.omico.gradm.internal.YamlDocument
import me.omico.gradm.internal.YamlObject
import me.omico.gradm.internal.asYamlDocument
import me.omico.gradm.internal.config.MutableFlatVersions
import me.omico.gradm.internal.find
import me.omico.gradm.internal.require
import me.omico.gradm.path.GradmProjectPaths
import me.omico.gradm.path.integrationRootDirectory
import me.omico.gradm.store
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.div

internal inline val GradmProjectPaths.githubIntegrationDirectory: Path
    get() = integrationRootDirectory / "github"

internal inline val GradmProjectPaths.githubIntegrationVersionsMetaFile: Path
    get() = githubIntegrationDirectory / "versions-meta.txt"

internal inline val GradmProjectPaths.localGithubIntegrationVersionsMeta: VersionsMeta?
    get() = githubIntegrationVersionsMetaFile.asVersionsMeta()

internal fun GradmProjectPaths.updateLocalVersionsMeta(versionsMeta: VersionsMeta) {
    githubIntegrationDirectory.createDirectories()
    versionsMeta.store(githubIntegrationVersionsMetaFile)
}

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

internal val GithubVersion.matchType: MatchType
    get() = find("matchType", "exact")
        .let(String::uppercase)
        .let(MatchType::valueOf)

internal data class GithubVersionConfiguration(
    val repository: String,
    val alias: String,
    val regex: Regex,
    val group: Int,
    val matchType: MatchType,
)

internal enum class MatchType {
    EXACT,
    PARTIAL,
}

internal fun GithubVersionConfiguration(configuration: GithubVersion): GithubVersionConfiguration =
    GithubVersionConfiguration(
        repository = configuration.repository,
        alias = configuration.alias ?: configuration.repository.substringAfter("/"),
        regex = configuration.regex?.toRegex() ?: defaultSemverRegex,
        group = configuration.group,
        matchType = configuration.matchType,
    )

internal fun GradmProjectPaths.parseGithubIntegration(configFile: Path, versions: MutableFlatVersions) =
    configFile.asYamlDocument().github?.versions
        ?.map(::GithubVersionConfiguration)
        ?.mapNotNull { configuration ->
            val tag = configuration.latestReleaseTag
            val result = when (configuration.matchType) {
                MatchType.EXACT -> configuration.regex.matchEntire(tag)
                MatchType.PARTIAL -> configuration.regex.find(tag)
            }
            if (result == null) {
                info { "[${configuration.repository}]: Unable to match regex ${configuration.regex} with tag $tag" }
                return@mapNotNull null
            }
            val key = "versions.${configuration.alias}"
            if (versions.contains(key)) {
                info { "[${configuration.repository}]: Duplicate with $key, skipping." }
                return@mapNotNull null
            }
            runCatching { result.groupValues[configuration.group] }
                .fold(
                    onSuccess = { key to it },
                    onFailure = {
                        info { "[${configuration.repository}]: Unable to parse version from regex ${configuration.regex} with tag $tag." }
                        null
                    },
                )
        }
        ?.toMap()
        ?.also(::updateLocalVersionsMeta)
        ?.forEach { (key, value) -> versions[key] = value }
        ?: Unit

private val GithubVersionConfiguration.latestReleaseTag
    get() = latestReleaseTag(repository)

private val defaultSemverRegex =
    "(^(0|[1-9]\\d*)\\.(0|[1-9]\\d*)\\.(0|[1-9]\\d*)(?:-((?:0|[1-9]\\d*|\\d*[a-zA-Z-][\\da-zA-Z-]*)(?:\\.(?:0|[1-9]\\d*|\\d*[a-zA-Z-][\\da-zA-Z-]*))*))?(?:\\+([\\da-zA-Z-]+(?:\\.[\\da-zA-Z-]+)*))?\$)".toRegex()
