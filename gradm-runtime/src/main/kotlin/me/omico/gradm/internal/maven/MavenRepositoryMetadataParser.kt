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
import me.omico.gradm.VersionsMeta
import me.omico.gradm.asVersionsMetaHash
import me.omico.gradm.internal.YamlDocument
import me.omico.gradm.internal.config.Library
import me.omico.gradm.internal.config.dependencies
import me.omico.gradm.internal.config.metadataLocalPath
import me.omico.gradm.internal.config.metadataUrl
import me.omico.gradm.internal.config.repositories
import me.omico.gradm.internal.config.repositoryUrl
import me.omico.gradm.internal.path.GradmPaths
import me.omico.gradm.internal.sha1
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.isRegularFile
import kotlin.io.path.writeBytes
import kotlin.io.path.writeText
import kotlin.streams.toList

object MavenRepositoryMetadataParser {

    var lastVersionsMetaHash: String? = null

    fun updateVersionsMeta(document: YamlDocument): VersionsMeta =
        hashMapOf<String, String>()
            .apply { document.downloadAllMetadata() }
            .apply { putAll(localVersionsMeta) }
            .also { updateLastVersionsMetaHash() }
            .also { document.storeAvailableUpdates(collectAllMetadata()) }

    val localVersionsMeta: VersionsMeta
        get() = collectAllMetadata().associate { it.module to it.latestVersion }

    private fun updateLastVersionsMetaHash() {
        lastVersionsMetaHash = GradmPaths.Metadata.versionsMetaHash.asVersionsMetaHash()
    }

    private fun YamlDocument.downloadAllMetadata() =
        runBlocking {
            if (GradmConfigs.offline) return@runBlocking
            val byteArrays = ArrayList<ByteArray>()
            val repositories = repositories
            dependencies.forEach { dependency ->
                val repositoryUrl = dependency.repositoryUrl(repositories)
                dependency.libraries.forEach { library -> byteArrays.add(library.downloadMetadata(repositoryUrl)) }
            }
            storeHash(byteArrays)
        }

    private suspend fun Library.downloadMetadata(repositoryUrl: String): ByteArray =
        run {
            val bytes = withContext(Dispatchers.IO) { metadataUrl(repositoryUrl.fixedUrl()).readBytes() }
            val metadataPath = metadataLocalPath(GradmPaths.Metadata.rootDir)
            metadataPath.parent.createDirectories()
            metadataPath.writeBytes(bytes)
            bytes
        }

    private fun collectAllMetadataFile(): List<Path> =
        Files.walk(GradmPaths.Metadata.rootDir)
            .filter { it.isRegularFile() && it.fileName.endsWith("maven-metadata.xml") }
            .toList()

    private fun collectAllMetadata(): List<MavenMetadata> =
        collectAllMetadataFile().map(::MavenMetadata)

    private fun String.fixedUrl(): String =
        when {
            endsWith("/") -> removeSuffix("/")
            else -> this
        }

    private fun storeHash(byteArrays: List<ByteArray>) =
        GradmPaths.Metadata.versionsMetaHash.writeText(byteArrays.sha1())
}
