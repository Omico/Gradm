import java.nio.file.Files
import java.nio.file.Path

plugins {
    id("gradm")
    id("gradm.build-logic.root-project.base")
}

val syncGradleEnterpriseVersion by tasks.registering {
    Files.walk(rootDir.toPath())
        .filter { it.endsWith("settings.gradle.kts") }
        .map(Path::toFile)
        .forEach { file ->
            buildString {
                file.readLines().forEach {
                    val line = when {
                        it.startsWith("    id(\"com.gradle.enterprise\") version") ->
                            "    id(\"com.gradle.enterprise\") version \"${versions.plugins.gradle.enterprise}\""
                        else -> it
                    }
                    appendLine(line)
                }
            }.let(file::writeText)
        }
}
