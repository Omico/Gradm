import me.omico.consensus.dsl.requireRootProject
import me.omico.gradm.project.internal.applyPluginVersion
import me.omico.gradm.project.internal.matchesPlugin

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
                    when {
                        line.matchesPlugin("me.omico.gradm") ->
                            applyPluginVersion("me.omico.gradm", properties["PROJECT_VERSION"].toString())
                        line.matchesPlugin("com.gradle.enterprise") ->
                            applyPluginVersion("com.gradle.enterprise", versions.plugins.gradle.enterprise)
                        else -> appendLine(line)
                    }
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
