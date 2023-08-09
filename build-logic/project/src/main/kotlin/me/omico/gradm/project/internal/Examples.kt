package me.omico.gradm.project.internal

internal fun String.replacePluginVersions(vararg pairs: Pair<String, String>): String =
    pairs.fold(this) { acc, (pluginId, version) -> acc.replacePluginVersion(pluginId, version) }

private fun String.replacePluginVersion(pluginId: String, version: String): String =
    replace(
        regex = """\s+id\("$pluginId"\)\s+version\s+"(.*)"""".toRegex(),
        replacement = "    id(\"$pluginId\") version \"$version\"",
    )
