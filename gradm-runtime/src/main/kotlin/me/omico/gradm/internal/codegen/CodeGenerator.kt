package me.omico.gradm.internal.codegen

import me.omico.gradm.internal.VersionsMeta
import me.omico.gradm.internal.YamlDocument
import me.omico.gradm.internal.config.Dependency
import me.omico.gradm.internal.config.Library
import me.omico.gradm.internal.config.alias
import me.omico.gradm.internal.config.dependencies
import me.omico.gradm.internal.config.gradmVersion
import me.omico.gradm.internal.path.GradmPaths
import me.omico.gradm.internal.path.RootProjectPaths
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes
import java.util.Locale
import me.omico.gradm.internal.codegen.Dependency as CodegenDependency
import me.omico.gradm.internal.codegen.Library as CodegenLibrary

internal fun generateDependenciesProjectFiles(document: YamlDocument, versionsMeta: VersionsMeta) {
    RootProjectPaths.copyTo(GradmPaths.GeneratedDependenciesProject)
    writeGradleBuildScript(document.gradmVersion)
    clearDir(GradmPaths.GeneratedDependenciesProject.sourceDir)
    createCodegenDependencies(document.dependencies, versionsMeta).values.forEach { dependency ->
        dependency.toFileSpec().writeTo(GradmPaths.GeneratedDependenciesProject.sourceDir)
        dependency.toDslFileSpec().writeTo(GradmPaths.GeneratedDependenciesProject.sourceDir)
    }
}

private fun writeGradleBuildScript(gradmVersion: String) {
    Files.write(
        GradmPaths.GeneratedDependenciesProject.gradleBuildScript,
        buildString {
            appendLine("//")
            appendLine("// Generated by Gradm, will be overwritten by every dependencies update, do not edit!!!")
            appendLine("//")
            appendLine()
            appendLine("plugins {")
            appendLine("    `embedded-kotlin`")
            appendLine("}")
            appendLine()
            appendLine("kotlin {")
            appendLine("    target.compilations.all {")
            appendLine("        kotlinOptions {")
            appendLine("            jvmTarget = \"11\"")
            appendLine("        }")
            appendLine("    }")
            appendLine("}")
            appendLine()
            appendLine("repositories {")
            appendLine("    mavenCentral()")
            appendLine("    mavenLocal()")
            appendLine("}")
            appendLine()
            appendLine("dependencies {")
            appendLine("    compileOnly(gradleApi())")
            appendLine("    compileOnly(gradleKotlinDsl())")
            appendLine("    implementation(\"me.omico.gradm:gradm-runtime:$gradmVersion\")")
            appendLine("}")
        }.toByteArray(),
    )
}

private fun clearDir(dir: Path) {
    if (Files.exists(dir)) {
        Files.walkFileTree(
            dir,
            object : SimpleFileVisitor<Path>() {
                override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {
                    Files.deleteIfExists(file)
                    return FileVisitResult.CONTINUE
                }
            }
        )
    }
    Files.createDirectories(dir)
}

private fun createCodegenDependencies(
    dependencies: List<Dependency>,
    versionsMeta: VersionsMeta,
): Map<String, CodegenDependency> {
    return hashMapOf<String, CodegenDependency>().apply {
        dependencies.forEach { dependency ->
            dependency.libraries.forEach { library ->
                getOrCreate(dependency.name).addLibrary(dependency.name, library, versionsMeta)
            }
        }
    }
}

private fun CodegenDependency.addLibrary(
    dependencyName: String,
    library: Library,
    versionsMeta: VersionsMeta,
) {
    val strings = library.alias().split(".", limit = 2)
    val subName = strings.first()
    val subAlias = strings.last()
    when (subName) {
        subAlias -> libraries.add(library.toCodegenLibrary(versionsMeta))
        else -> {
            val subDependencyName = "${dependencyName.capitalize()}${subName.capitalize()}"
            subDependencies
                .getOrCreate(subName, subDependencyName)
                .addLibrary(
                    dependencyName = subDependencyName,
                    library = library.copy(alias = subAlias),
                    versionsMeta = versionsMeta,
                )
        }
    }
}

private fun Library.toCodegenLibrary(versionsMeta: VersionsMeta): CodegenLibrary =
    CodegenLibrary(
        module = module,
        alias = alias(),
        version = version ?: versionsMeta[module]!!,
    )

private fun HashMap<String, CodegenDependency>.getOrCreate(
    key: String,
    dependencyName: String = key,
): CodegenDependency =
    this[key] ?: CodegenDependency(
        name = dependencyName,
        libraries = arrayListOf(),
        subDependencies = hashMapOf(),
    ).also { this[key] = it }

private fun String.capitalize() =
    replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
