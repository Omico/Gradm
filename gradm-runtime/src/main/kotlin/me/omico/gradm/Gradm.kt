package me.omico.gradm

import me.omico.gradm.internal.path.GradmPaths
import me.omico.gradm.internal.path.RootProjectPaths
import java.nio.file.Path
import kotlin.io.path.exists

const val GRADM_DEPENDENCY_PACKAGE_NAME = "me.omico.gradm.dependency"

val hasGradmConfig: Boolean
    get() = RootProjectPaths.gradmConfig.exists()

val isGradmGeneratedDependenciesSourcesExists: Boolean
    get() = GradmPaths.GeneratedDependenciesProject.sourceDir.exists()

val gradmMetadataDir: Path
    get() = GradmPaths.Metadata.rootDir

val gradmGeneratedDependenciesDir: Path
    get() = GradmPaths.GeneratedDependenciesProject.rootDir

val gradmGeneratedDependenciesBuildDir: Path
    get() = GradmPaths.GeneratedDependenciesProject.buildDir

val gradmGeneratedDependenciesSourceDir: Path
    get() = GradmPaths.GeneratedDependenciesProject.sourceDir
