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
package me.omico.gradm.integration.github

import me.omico.gradm.GradmConfiguration
import me.omico.gradm.VersionsMeta
import me.omico.gradm.asVersionsMeta
import me.omico.gradm.info
import me.omico.gradm.integration.GradmIntegrationHolder
import me.omico.gradm.integration.GradmIntegrationPlugin
import me.omico.gradm.integration.GradmProjectWithIntegration
import me.omico.gradm.integration.createExtension
import me.omico.gradm.integration.github.internal.GithubVersionConfiguration
import me.omico.gradm.integration.github.internal.GradmGitHubIntegrationExtensionImpl
import me.omico.gradm.integration.github.internal.github
import me.omico.gradm.integration.github.internal.versionPair
import me.omico.gradm.integration.github.internal.versions
import me.omico.gradm.store

@Suppress("unused")
class GradmGitHubIntegrationPlugin : GradmIntegrationPlugin<GradmGitHubIntegrationExtension>() {
    override val id: String = "github"

    override fun GradmProjectWithIntegration<GradmGitHubIntegrationExtension>.onApply() {
        val extension = createExtension<GradmGitHubIntegrationExtension, GradmGitHubIntegrationExtensionImpl>()
        registerExtension(extension)
        registerInput(CACHED_VERSIONS_FILE_PATH)
        registerOutput(CACHED_VERSIONS_FILE_PATH)
    }

    override fun GradmIntegrationHolder.onGenerate(): Unit =
        when (val versionsMeta = localGithubIntegrationVersionsMeta) {
            null -> applyRemoteVersions()
            else -> applyVersions(versionsMeta)
        }

    override fun GradmIntegrationHolder.onRefresh(): Unit =
        when {
            GradmConfiguration.offline -> applyCachedVersions()
            else -> applyRemoteVersions()
        }

    private fun GradmIntegrationHolder.applyRemoteVersions() {
        val github = integrationConfiguration.github ?: return
        val versionConfigurations = github.versions.map(::GithubVersionConfiguration)
        val versionsMeta = versionConfigurations
            .mapNotNull { configuration -> configuration.versionPair(versions) }
            .toMap()
        storeCache(versionsMeta)
        applyVersions(versionsMeta)
    }

    private fun GradmIntegrationHolder.applyCachedVersions() {
        info { "Github integration is running in offline mode." }
        val versionsMeta = localGithubIntegrationVersionsMeta
        if (versionsMeta == null) {
            info { "The execution of the GitHub integration will be skipped. This may cause the task to fail." }
            return
        }
        applyVersions(versionsMeta)
    }

    private fun GradmIntegrationHolder.applyVersions(versionsMeta: VersionsMeta): Unit =
        versionsMeta.forEach { (key, value) ->
            when {
                key in versions -> info { "$key is already in the versions, skipping." }
                else -> versions[key] = value
            }
        }

    private inline val GradmIntegrationHolder.localGithubIntegrationVersionsMeta: VersionsMeta?
        get() = outputFile(CACHED_VERSIONS_FILE_PATH).asVersionsMeta()

    private fun GradmIntegrationHolder.storeCache(versionsMeta: VersionsMeta): Unit =
        versionsMeta.store(outputFile(CACHED_VERSIONS_FILE_PATH))

    companion object {
        private const val CACHED_VERSIONS_FILE_PATH = "versions"
    }
}
