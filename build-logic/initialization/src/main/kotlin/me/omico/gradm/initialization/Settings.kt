package me.omico.gradm.initialization

import org.gradle.api.initialization.Settings

internal fun Settings.includeGradm(path: String) {
    include(path)
    project(path).name = path.removePrefix(":").replace(":", "-")
}
