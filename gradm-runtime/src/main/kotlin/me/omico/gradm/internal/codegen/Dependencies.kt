package me.omico.gradm.internal.codegen

import me.omico.gradm.internal.VersionsMeta
import me.omico.gradm.internal.YamlDocument
import me.omico.gradm.internal.config.Dependency
import me.omico.gradm.internal.config.dependencies
import me.omico.gradm.internal.path.GradmPaths

internal fun generateDependenciesSourceFiles(document: YamlDocument, versionsMeta: VersionsMeta) {
    val codegenDependencies = createCodegenDependencies(document.dependencies, versionsMeta)
    codegenDependencies.forEach { dependency ->
        dependency.toFileSpec().writeTo(GradmPaths.GeneratedDependenciesProject.sourceDir)
    }
    codegenDependencies.toDslFileSpec().writeTo(GradmPaths.GeneratedDependenciesProject.sourceDir)
}

private fun createCodegenDependencies(
    dependencies: List<Dependency>,
    versionsMeta: VersionsMeta,
): List<CodegenDependency> =
    hashMapOf<String, CodegenDependency>()
        .apply {
            dependencies.forEach { dependency ->
                dependency.libraries.forEach { library ->
                    getOrCreate(dependency.name).addLibrary(dependency.name, library, versionsMeta)
                }
            }
        }
        .values
        .toList()
