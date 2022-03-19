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

import org.snakeyaml.engine.v2.api.Load
import org.snakeyaml.engine.v2.api.LoadSettings
import org.snakeyaml.engine.v2.api.LoadSettingsBuilder
import java.io.InputStream

typealias YamlObject = Map<String, Any>
typealias YamlArray = List<YamlObject>
typealias YamlDocument = YamlObject

fun InputStream.asYamlDocument(builder: LoadSettingsBuilder.() -> Unit = {}): YamlDocument =
    loadDocument(this, builder)

inline fun <reified T> YamlDocument.find(key: String): T? = this[key] as T

inline fun <reified T> YamlDocument.find(key: String, defaultValue: T): T = find<T>(key) ?: defaultValue

inline fun <reified T> YamlDocument.find(key: String, orElse: () -> T): T = find<T>(key) ?: orElse()

inline fun <reified T> YamlDocument.require(key: String): T = find<T>(key) ?: error("$key not found.")

private inline fun <reified T> loadDocument(
    inputStream: InputStream,
    builder: LoadSettingsBuilder.() -> Unit = {},
): T = LoadSettings.builder().apply(builder).build().let(::Load).loadFromInputStream(inputStream) as T
