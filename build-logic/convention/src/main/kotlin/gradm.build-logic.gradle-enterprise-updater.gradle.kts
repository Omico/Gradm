import java.nio.file.Files

plugins {
    id("gradm.build-logic.root-project.base")
}

val syncGradleEnterpriseVersion by tasks.registering {
    Files.walk(rootDir.toPath())
        .filter { it.endsWith("settings.gradle.kts") }
        .map { it.toFile() }
        .forEach { file ->
            buildString {
                file.readLines().forEach {
                    val line = when {
                        it.startsWith("    id(\"com.gradle.enterprise\") version") ->
                            "    id(\"com.gradle.enterprise\") version \"${properties["versions.gradle.enterprise"]}\""
                        else -> it
                    }
                    appendLine(line)
                }
            }.let { file.writeText(it) }
        }
}
