package me.omico.gradm.internal

import org.gradle.api.initialization.Settings
import java.nio.file.Path

object ProjectConfig {

    lateinit var rootDirPath: Path

    var isOffline: Boolean = false

    fun initialize(settings: Settings) {
        this.rootDirPath = settings.rootDir.toPath()
        this.isOffline = settings.startParameter.isOffline
    }
}
