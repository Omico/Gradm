import me.omico.consensus.api.dsl.requireRootProject
import me.omico.gradm.project.internal.replacePluginVersions

plugins {
    id("gradm.gradm")
}

requireRootProject()

val syncExamples by tasks.registering {
    file("examples").walk()
        .filter { it.endsWith("settings.gradle.kts") || it.endsWith("build.gradle.kts") }
        .forEach { file ->
            buildString {
                file.readLines().forEach { line ->
                    line
                        .replacePluginVersions(
                            "me.omico.gradm" to properties["project.version"].toString(),
                            "me.omico.gradm.integration.github" to properties["project.version"].toString(),
                            "com.gradle.develocity" to versions.plugins.develocity,
                        )
                        .let(::appendLine)
                }
            }.let(file::writeText)
        }
    listOf(
        "examples/gradm-getting-started/gradm/gradm3.yml",
        "examples/gradm-with-composite-build/build-logic/gradm/gradm.yml",
        "examples/gradm-with-kotlin-multiplatform/gradm/gradm.yml",
    ).forEach {
        file("examples/template/gradm.yml").copyTo(file(it), overwrite = true)
    }
}
