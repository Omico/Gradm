/*
 * Copyright 2022 Omico
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
