import me.omico.build.gitPreCommitHook

plugins {
    id("gradm.build-logic.root-project.base")
}

rootDir.addPreCommitHook()

fun File.addPreCommitHook() {
    if (!gitPreCommitHook.parentFile.exists()) gitPreCommitHook.parentFile.mkdirs()
    if (!gitPreCommitHook.isFile) gitPreCommitHook.delete()
    buildString {
        appendLine("#!/bin/sh")
        appendLine()
        if (file("gradlew").exists()) {
            appendLine("# Chmod Gradle wrapper")
            appendLine("git ls-files \"*gradlew\" | xargs git update-index --add --chmod=+x")
        }
    }.let(gitPreCommitHook::writeText)
}
