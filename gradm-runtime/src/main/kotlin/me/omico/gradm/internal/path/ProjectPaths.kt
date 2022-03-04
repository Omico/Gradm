package me.omico.gradm.internal.path

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

internal abstract class ProjectPaths(
    val rootDir: Path,
) {
    val buildDir = rootDir.resolve("build")

    private val gradleWrapperDir: Path = rootDir.resolve("gradle/wrapper")
    private val gradleWrapperJar: Path = gradleWrapperDir.resolve("gradle-wrapper.jar")
    private val gradleWrapperProperties: Path = gradleWrapperDir.resolve("gradle-wrapper.properties")

    private val gradlew: Path = rootDir.resolve("gradlew")
    private val gradlewBat: Path = rootDir.resolve("gradlew.bat")

    fun copyTo(other: ProjectPaths, vararg options: StandardCopyOption = arrayOf(StandardCopyOption.REPLACE_EXISTING)) {
        Files.createDirectories(other.rootDir)
        Files.createDirectories(other.gradleWrapperDir)
        Files.copy(gradleWrapperJar, other.gradleWrapperJar, *options)
        Files.copy(gradleWrapperProperties, other.gradleWrapperProperties, *options)
        Files.copy(gradlew, other.gradlew, *options)
        Files.copy(gradlewBat, other.gradlewBat, *options)
    }
}
