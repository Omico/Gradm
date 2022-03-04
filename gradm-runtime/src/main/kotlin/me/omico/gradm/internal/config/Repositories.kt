package me.omico.gradm.internal.config

import me.omico.gradm.internal.YamlArray
import me.omico.gradm.internal.YamlDocument
import me.omico.gradm.internal.YamlObject
import me.omico.gradm.internal.find
import me.omico.gradm.internal.require

val YamlDocument.repositories: List<Repository>
    get() = find<YamlArray>("repositories", emptyList())
        .map(::Repository)

data class Repository(
    val id: String,
    val url: String,
)

private fun Repository(repository: YamlObject): Repository =
    Repository(
        id = repository.require("id"),
        url = repository.require("url"),
    )
