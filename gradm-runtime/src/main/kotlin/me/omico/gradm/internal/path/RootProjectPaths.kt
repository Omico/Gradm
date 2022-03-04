package me.omico.gradm.internal.path

import me.omico.gradm.internal.ProjectConfig
import java.nio.file.Path

internal object RootProjectPaths : ProjectPaths(
    rootDir = ProjectConfig.rootDirPath,
) {
    val gradmConfig: Path by lazy { rootDir.resolve("gradm.yml") }
}
