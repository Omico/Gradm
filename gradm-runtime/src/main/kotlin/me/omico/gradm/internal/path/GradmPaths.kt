package me.omico.gradm.internal.path

import java.nio.file.Path

internal object GradmPaths {

    val rootDir: Path by lazy { RootProjectPaths.rootDir.resolve(".gradm") }

    object Metadata {
        val rootDir: Path by lazy { GradmPaths.rootDir.resolve("metadata") }
        val versionsMeta: Path by lazy { rootDir.resolve("versions-meta.txt") }
        val versionsMetaHash: Path by lazy { rootDir.resolve("versions-meta.hash") }
    }

    object GeneratedDependenciesProject : ProjectPaths(
        rootDir = rootDir.resolve("generated-dependencies"),
    ) {
        val sourceDir: Path by lazy { rootDir.resolve("src/main/kotlin") }
        val gradleBuildScript: Path by lazy { rootDir.resolve("build.gradle.kts") }
    }
}
