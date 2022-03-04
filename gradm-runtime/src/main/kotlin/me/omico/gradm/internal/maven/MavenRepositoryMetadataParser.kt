package me.omico.gradm.internal.maven

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import me.omico.gradm.internal.ProjectConfig
import me.omico.gradm.internal.VersionsMeta
import me.omico.gradm.internal.config.Dependency
import me.omico.gradm.internal.config.Repository
import me.omico.gradm.internal.config.metadataLocalPath
import me.omico.gradm.internal.config.metadataUrl
import me.omico.gradm.internal.config.repositoryUrl
import me.omico.gradm.internal.path.GradmPaths
import me.omico.gradm.internal.store
import me.omico.gradm.internal.versionsMetaHash
import java.net.URL
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.stream.Stream
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.io.path.exists
import kotlin.io.path.isRegularFile

internal object MavenRepositoryMetadataParser {

    var lastVersionsMetaHash: String? = null

    private val documentBuilder: DocumentBuilder by lazy { DocumentBuilderFactory.newInstance().newDocumentBuilder() }

    fun updateVersionsMeta(dependencies: List<Dependency>, repositories: List<Repository>): VersionsMeta =
        hashMapOf<String, String>()
            .apply { if (!ProjectConfig.isOffline) downloadAllMetadata(dependencies, repositories) }
            .apply { loadAllMetadata().forEach { this[it.module] = it.latestVersion } }
            .also(VersionsMeta::store)

    fun updateLastVersionsMetaHash() {
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
                        URL(libraryMeta.metadataUrl(repositoryUrl)).openStream()
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
}
