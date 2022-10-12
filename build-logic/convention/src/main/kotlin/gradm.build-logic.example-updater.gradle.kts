import java.nio.file.Files

plugins {
    id("gradm.build-logic.root-project.base")
}

val syncExamples by tasks.registering {
    Files.walk(file("examples").toPath())
        .filter { it.endsWith("settings.gradle.kts") || it.endsWith("build.gradle.kts") }
        .map { it.toFile() }
        .forEach { file ->
            buildString {
                file.readLines().forEach {
                    val line = when {
                        it.startsWith("    id(\"me.omico.gradm\") version") ->
                            "    id(\"me.omico.gradm\") version \"${properties["PROJECT_VERSION"]}\""
                        else -> it
                    }
                    appendLine(line)
                }
            }.let { file.writeText(it) }
        }
    listOf(
        "examples/gradm-getting-started/gradm.yml",
        "examples/gradm-getting-started-v3/gradm/gradm3.yml",
        "examples/gradm-with-build-logic/build-logic/gradm.yml",
        "examples/gradm-with-buildSrc/gradm.yml",
    ).forEach {
        file("examples/template/gradm.yml").copyTo(file(it), overwrite = true)
    }
}
