package me.omico.gradm.internal.maven

import java.nio.file.Path
import javax.xml.parsers.DocumentBuilder

data class MavenMetadata(
    val group: String,
    val artifact: String,
    val latestVersion: String,
) {
    val module: String by lazy { "$group:$artifact" }
}

internal fun DocumentBuilder.MavenMetadata(metadataPath: Path): MavenMetadata =
    parse(metadataPath.toFile())
        .let { document ->
            MavenMetadata(
                group = document.getElementsByTagName("groupId").item(0).textContent,
                artifact = document.getElementsByTagName("artifactId").item(0).textContent,
                latestVersion = document.getElementsByTagName("latest").item(0).textContent,
            )
        }
