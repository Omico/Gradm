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
package me.omico.gradm.internal.config

import me.omico.gradm.internal.YamlDocument
import me.omico.gradm.internal.YamlObject
import me.omico.gradm.internal.find

typealias Versions = Map<String, String>

val YamlDocument.versions: Versions
    get() = hashMapOf<String, String>()
        .apply { putAll(this@versions.find<YamlObject>("versions", emptyMap()).crateFixedVersionsMap()) }

private fun Any?.crateFixedVersionsMap(
    parentKey: String = "versions",
    map: HashMap<String, String> = HashMap(),
): Versions = run {
    when (this) {
        is Map<*, *> -> forEach { (key, value) ->
            val fixedKey = fixedKey(key)
            when (value) {
                is String -> map["$parentKey.$fixedKey"] = value
                is Map<*, *> -> value.crateFixedVersionsMap("$parentKey.$fixedKey", map)
            }
        }
    }
    map
}

private val fixedKeyRegex = Regex("^[a-zA-Z0-9]+([.-][a-zA-Z0-9]+)?")

private fun fixedKey(key: Any?): String = run {
    require(key is String) { "Key must be String." }
    require(fixedKeyRegex.matches(key)) { "Illegal key found, key: [$key]." }
    key.replace("-", ".")
}
