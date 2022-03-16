package me.omico.gradm.internal

import me.omico.gradm.internal.path.GradmPaths
import java.nio.file.Files
import java.nio.file.Path
import java.security.MessageDigest

typealias VersionsMeta = Map<String, String>

fun VersionsMeta.store() {
    Files.write(
        GradmPaths.Metadata.versionsMeta,
        buildString { this@store.toSortedMap().forEach { (key, value) -> appendLine("$key=$value") } }.toByteArray(),
    )
    Files.write(
        GradmPaths.Metadata.versionsMetaHash,
        GradmPaths.Metadata.versionsMeta.sha1().toByteArray(),
    )
}

val localVersionsMeta: VersionsMeta
    get() = Files.readAllLines(GradmPaths.Metadata.versionsMeta)
        .associate {
            val strings = it.split("=")
            strings.first() to strings.last()
        }

val versionsMetaHash: String
    get() = Files.readString(GradmPaths.Metadata.versionsMetaHash)

private fun Path.sha1(): String = Files.readAllBytes(this).hash("SHA-1")

private fun ByteArray.hash(algorithm: String): String =
    MessageDigest
        .getInstance(algorithm)
        .digest(this)
        .fold("") { str, it -> str + "%02x".format(it) }
