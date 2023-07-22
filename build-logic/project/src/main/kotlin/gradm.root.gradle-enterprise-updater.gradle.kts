import me.omico.consensus.dsl.requireRootProject

plugins {
    id("gradm.gradm")
}

requireRootProject()

val syncGradleEnterpriseVersion by tasks.registering {
    rootDir.walk()
        .filter { it.endsWith("settings.gradle.kts") }
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
