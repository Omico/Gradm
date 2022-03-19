package me.omico.gradm.internal.config

import me.omico.gradm.internal.YamlArray
import me.omico.gradm.internal.YamlObject
import me.omico.gradm.internal.find
import me.omico.gradm.internal.require
import java.net.URL
import java.nio.file.Path

internal fun Versions.createLibraries(dependencyObject: DependencyObject): List<Library> =
    dependencyObject.require<YamlArray>("libraries").map(::Library)

data class Library(
    val module: String,
    val version: String?,
    internal val alias: String?,
) {
    val group: String by lazy { module.split(':').first() }
    val artifact: String by lazy { module.split(':').last() }
}

fun Library.alias(): String = alias ?: artifact.replace("-", ".")

internal fun Library.metadataUrl(repositoryUrl: String): URL =
    URL("$repositoryUrl/${group.replace(".", "/")}/$artifact/maven-metadata.xml")

internal fun Library.metadataLocalPath(metadataRootDir: Path): Path =
    metadataRootDir.resolve(group).resolve(artifact).resolve("maven-metadata.xml")

internal fun Versions.Library(library: YamlObject): Library =
    Library(
        module = when {
            library.containsKey("module") -> library.require("module")
            else -> "${library.require<String>("group")}:${library.require<String>("artifact")}"
        },
        alias = library.find("alias"),
        version = fixedVersion(library.find("version"), this),
    )

private val fixedVersionRegex = Regex("(\\\$\\{(.*)})")

private fun fixedVersion(version: String?, versions: Versions): String? =
    version
        ?.let { fixedVersionRegex.matchEntire(it) }
        ?.let { it.groupValues[2] }
        ?.let { versions[it] }
        ?: version
