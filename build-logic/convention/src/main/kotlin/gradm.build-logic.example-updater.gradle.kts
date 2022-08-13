import java.nio.file.Files

plugins {
    id("gradm.build-logic.root-project.base")
}

val syncGradmVersionForExamples by tasks.registering {
    Files.walk(file("examples").toPath())
        .filter { it.endsWith("settings.gradle.kts") }
        .map { it.toFile() }
        .forEach { file ->
            buildString {
                file.readLines().forEach {
                    val line = when {
                        it.startsWith("    id(\"me.omico.gradm\") version") ->
                            "    id(\"me.omico.gradm\") version \"${version}\""
                        else -> it
                    }
                    appendLine(line)
                }
            }.let { file.writeText(it) }
        }
}
