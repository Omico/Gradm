import me.omico.consensus.dsl.requireRootProject

requireRootProject()

val syncExamples by tasks.registering {
    file("examples").walk()
        .filter { it.endsWith("settings.gradle.kts") || it.endsWith("build.gradle.kts") }
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
