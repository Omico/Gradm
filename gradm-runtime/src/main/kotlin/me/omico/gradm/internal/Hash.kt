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
package me.omico.gradm.internal

import java.nio.file.Files
import java.nio.file.Path
import java.security.MessageDigest
import java.util.stream.Stream
import kotlin.io.path.isDirectory
import kotlin.io.path.isRegularFile
import kotlin.io.path.readBytes

fun Path.hash(algorithm: String): String =
    when {
        isRegularFile() -> readBytes().hash(algorithm)
        isDirectory() -> Files.walk(this).filter(Path::isRegularFile).map(Path::readBytes).hash(algorithm)
        else -> error("$this is not a file or directory")
    }

fun hash(algorithm: String, block: MessageDigest.() -> Unit): String =
    MessageDigest.getInstance(algorithm).apply(block).digest().hex()

fun ByteArray.hash(algorithm: String): String = hash(algorithm) { update(this@hash) }

fun Stream<ByteArray>.hash(algorithm: String): String = hash(algorithm) { forEach(::update) }

fun Iterable<ByteArray>.hash(algorithm: String): String = hash(algorithm) { forEach(::update) }

fun ByteArray.hex(): String = fold("") { str, it -> str + "%02x".format(it) }

fun Path.sha1(): String = hash("SHA-1")

fun Iterable<ByteArray>.sha1(): String = hash("SHA-1")
