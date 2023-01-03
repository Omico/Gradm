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
package me.omico.gradm.integration.github

import me.omico.gradm.GradmConfiguration
import me.omico.gradm.info
import me.omico.gradm.integration.GradmIntegration
import me.omico.gradm.integration.GradmIntegrationConfiguration
import me.omico.gradm.integration.github.internal.localGithubIntegrationVersionsMeta
import me.omico.gradm.integration.github.internal.parseGithubIntegration
import me.omico.gradm.integration.integrationConfigFile
import me.omico.gradm.internal.config.MutableFlatVersions
import me.omico.gradm.path.GradmProjectPaths
import java.nio.file.Path
import kotlin.io.path.exists

class GradmGithubIntegration : GradmIntegration {
    override val id: String = "github"
    override fun applyVersions(
        gradmProjectPaths: GradmProjectPaths,
        configuration: GradmIntegrationConfiguration,
        versions: MutableFlatVersions,
    ) {
        val configFile = gradmProjectPaths.integrationConfigFile(configuration)
        when {
            !configFile.exists() -> return
            GradmConfiguration.offline -> {
                info { "Github integration config found, but the offline mode is enabled." }
                gradmProjectPaths.applyVersionsByCache(versions)
            }
            else -> gradmProjectPaths.applyVersionsIfNeeded(configFile, versions)
        }
    }
}

private fun GradmProjectPaths.applyVersionsByCache(versions: MutableFlatVersions) =
    when (val versionsMeta = localGithubIntegrationVersionsMeta) {
        null -> info { "versions-meta.txt for Github integration is not found, skipping." }
        else -> versionsMeta.forEach { (key, value) ->
            when {
                versions.containsKey(key) -> info { "$key is already in the versions, skipping." }
                else -> versions[key] = value
            }
        }
    }

private fun GradmProjectPaths.applyVersionsIfNeeded(configFile: Path, versions: MutableFlatVersions) =
    when {
        GradmConfiguration.requireRefresh || localGithubIntegrationVersionsMeta == null ->
            parseGithubIntegration(configFile, versions)
        else -> applyVersionsByCache(versions)
    }
