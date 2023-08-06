package me.omico.gradm.project.internal

internal fun String.matchesPlugin(pluginId: String): Boolean = startsWith("    id(\"$pluginId\") version")

internal fun StringBuilder.applyPluginVersion(pluginId: String, version: String) {
    appendLine("    id(\"$pluginId\") version \"$version\"")
}
