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

import java.io.BufferedReader
import java.io.File
import java.nio.charset.Charset

fun command(
    charset: Charset = Charsets.UTF_8,
    shell: Shell,
    directory: String = ".",
    environment: MutableMap<String, String>.() -> Unit = {},
    onInput: (BufferedReader) -> Unit = { it.forEachLine(::println) },
    onError: (BufferedReader) -> Unit = { throw CommandExecutionException("Command failed: ${it.readText()}") },
    vararg commands: String,
) {
    val combinedCommands = ArrayList<String>()
        .apply { add(shell.name) }
        .apply { addAll(shell.arguments) }
        .apply { add(commands.joinToString(" ")) }
    val process = ProcessBuilder(combinedCommands)
        .directory(File(directory))
        .apply { environment().apply(environment) }
        .redirectOutput(ProcessBuilder.Redirect.PIPE)
        .redirectError(ProcessBuilder.Redirect.PIPE)
        .start()
    process.inputStream.bufferedReader(charset).apply(onInput)
    if (process.waitFor() == 0) return
    process.errorStream.bufferedReader(charset).apply(onError)
}

class CommandExecutionException(message: String) : RuntimeException(message)
