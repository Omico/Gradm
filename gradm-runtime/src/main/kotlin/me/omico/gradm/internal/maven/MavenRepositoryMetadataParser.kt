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
package me.omico.gradm.internal.maven

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import me.omico.gradm.GradmConfigs
import me.omico.gradm.internal.VersionsMeta
import me.omico.gradm.internal.YamlDocument
import me.omico.gradm.internal.config.Dependency
import me.omico.gradm.internal.config.Repository
import me.omico.gradm.internal.config.dependencies
import me.omico.gradm.internal.config.metadataLocalPath
import me.omico.gradm.internal.config.metadataUrl
import me.omico.gradm.internal.config.repositories
import me.omico.gradm.internal.config.repositoryUrl
import me.omico.gradm.internal.path.GradmPaths
import me.omico.gradm.internal.store
import me.omico.gradm.internal.versionsMetaHash
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.stream.Stream
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.io.path.exists
import kotlin.io.path.isRegularFile

object MavenRepositoryMetadataParser {

    var lastVersionsMetaHash: String? = null

    private val documentBuilder: DocumentBuilder by lazy { DocumentBuilderFactory.newInstance().newDocumentBuilder() }

    fun updateVersionsMeta(document: YamlDocument): VersionsMeta =
        hashMapOf<String, String>()
            .apply { if (!GradmConfigs.offline) downloadAllMetadata(document.dependencies, document.repositories) }
            .apply { loadAllMetadata().forEach { this[it.module] = it.latestVersion } }
            .also { updateLastVersionsMetaHash() }
            .also(VersionsMeta::store)

    private fun updateLastVersionsMetaHash() {
        lastVersionsMetaHash = when {
            GradmPaths.Metadata.versionsMetaHash.exists() -> versionsMetaHash
            else -> null
        }
    }

    private fun downloadAllMetadata(dependencies: List<Dependency>, repositories: List<Repository>) =
        runBlocking {
            dependencies.forEach { dependency ->
                val repositoryUrl = dependency.repositoryUrl(repositories)
                dependency.libraries.forEach { libraryMeta ->
                    val stream = withContext(Dispatchers.IO) {
                        libraryMeta.metadataUrl(repositoryUrl.fixedUrl()).openStream()
                    }
                    val metadataPath = libraryMeta.metadataLocalPath(GradmPaths.Metadata.rootDir)
                    Files.createDirectories(metadataPath.parent)
                    Files.copy(stream, metadataPath, StandardCopyOption.REPLACE_EXISTING)
                }
            }
        }

    private fun loadAllMetadata(): Stream<MavenMetadata> =
        Files.walk(GradmPaths.Metadata.rootDir)
            .filter { it.isRegularFile() && it.fileName.endsWith("maven-metadata.xml") }
            .map(documentBuilder::MavenMetadata)

    private fun String.fixedUrl(): String =
        when {
            endsWith("/") -> removeSuffix("/")
            else -> this
        }
}
