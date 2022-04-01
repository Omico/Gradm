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
package me.omico.gradm.integration.github

import me.omico.gradm.GradmConfigs
import me.omico.gradm.integration.GradmIntegration
import me.omico.gradm.integration.github.internal.parseGithubIntegration
import me.omico.gradm.internal.config.MutableFlatVersions
import me.omico.gradm.projectRootDir
import kotlin.io.path.exists

object GradmGithubIntegration : GradmIntegration() {

    private val githubIntegrationConfig = projectRootDir.resolve("gradm.integration.github.yml")

    override fun applyVersions(versions: MutableFlatVersions) {
        if (!githubIntegrationConfig.exists()) return
        when {
            GradmConfigs.offline -> {
                println("[Gradm]: Github integration config found, but the offline mode is enabled.")
                applyVersionsByCache(versions)
            }
            else -> applyVersionsIfNeeded(versions)
        }
    }

    private fun applyVersionsByCache(versions: MutableFlatVersions) =
        when (val versionsMeta = GradmGithubIntegrationConfigs.localVersionsMeta) {
            null -> println("[Gradm]: versions-meta.txt for Github integration is not found, skipping.")
            else -> versionsMeta.forEach { (key, value) ->
                when {
                    versions.containsKey(key) -> println("[Gradm]: $key is already in the versions, skipping.")
                    else -> versions[key] = value
                }
            }
        }

    private fun applyVersionsIfNeeded(versions: MutableFlatVersions) {
        when {
            GradmConfigs.updateDependencies || GradmGithubIntegrationConfigs.localVersionsMeta == null ->
                githubIntegrationConfig.parseGithubIntegration(versions)
            else -> applyVersionsByCache(versions)
        }
    }
}
