package me.omico.gradm.internal.config

import me.omico.gradm.internal.YamlArray
import me.omico.gradm.internal.YamlDocument
import me.omico.gradm.internal.YamlObject
import me.omico.gradm.internal.find
import me.omico.gradm.internal.require

val YamlDocument.dependencies: List<Dependency>
    get() = find<YamlArray>("dependencies", emptyList())
        .map(versions::Dependency)

data class Dependency(
    val name: String,
    val repository: String,
    val libraries: List<Library>,
)

internal fun Dependency.repositoryUrl(repositories: List<Repository>): String =
    repositories.find { it.id == repository }?.url ?: error("Repository not found.")

internal typealias DependencyObject = YamlObject

private fun Versions.Dependency(dependencyObject: DependencyObject): Dependency =
    Dependency(
        name = dependencyObject.require("name"),
        repository = dependencyObject.require("repository"),
        libraries = createLibraries(dependencyObject),
    )
