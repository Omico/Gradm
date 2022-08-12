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
package me.omico.gradm.utility

import java.io.File

fun command(
    shell: Shell,
    directory: String = ".",
    vararg commands: String,
) {
    val process = ProcessBuilder(shell.name, *shell.arguments, *commands)
        .directory(File(directory))
        .redirectOutput(ProcessBuilder.Redirect.PIPE)
        .redirectError(ProcessBuilder.Redirect.PIPE)
        .start()
    process.inputStream.bufferedReader().forEachLine { println(it) }
    if (process.exitValue() == 0) return
    val errorOutput = process.errorStream.bufferedReader().readText()
    throw RuntimeException("Command failed: $errorOutput")
}

fun gradleCommand(directory: String, vararg arguments: String) =
    command(
        shell = decideShell(),
        directory = directory,
        commands = arrayOf("gradlew", *arguments),
    )

private fun decideShell(): Shell = when {
    System.getProperty("os.name").startsWith("Windows") -> Shell.Cmd
    else -> Shell.Bash
}
