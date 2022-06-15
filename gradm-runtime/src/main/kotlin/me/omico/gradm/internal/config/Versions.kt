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
import me.omico.gradm.internal.find

typealias Versions = Map<String, Any>

val YamlDocument.versions: Versions
    get() = find("versions", emptyMap())

typealias FlatVersions = Map<String, String>
typealias MutableFlatVersions = MutableMap<String, String>

fun Versions.toFlatVersions(): FlatVersions = toMutableFlatVersions("versions", mutableMapOf())

fun FlatVersions.toMutableFlatVersions(): MutableFlatVersions = toMutableMap()

private fun Map<*, *>.toMutableFlatVersions(parentKey: String, versions: MutableFlatVersions): MutableFlatVersions =
    run {
        forEach { (key, value) ->
            val subKey = subFlatKey(key)
            when (value) {
                is String -> versions["$parentKey.$subKey"] = value
                is Map<*, *> -> value.toMutableFlatVersions("$parentKey.$subKey", versions)
            }
        }
        versions
    }

private val flatKeyRegex = Regex("^[a-zA-Z\\d]+([.-][a-zA-Z\\d]+)?")

private fun subFlatKey(key: Any?): String =
    run {
        require(key is String) { "Key must be String." }
        require(flatKeyRegex.matches(key)) { "Illegal key found, key: [$key]." }
        key.replace("-", ".")
    }

typealias SubTreeVersions = Map<String, TreeVersions>
typealias SubMutableTreeVersions = HashMap<String, MutableTreeVersions>

data class TreeVersions(
    val name: String,
    val version: String?,
    val subTreeVersions: SubTreeVersions,
)

data class MutableTreeVersions(
    val name: String,
    var version: String?,
    val subTreeVersions: SubMutableTreeVersions,
)

@JvmName("Versions_toTreeVersions")
fun Versions.toTreeVersions(): TreeVersions = toMutableTreeVersions().toTreeVersions()

@JvmName("Versions_toMutableTreeVersions")
fun Versions.toMutableTreeVersions(): MutableTreeVersions =
    createMutableTreeVersions {
        this@toMutableTreeVersions.forEach {
            groupTreeVersion(it.key, it.value)
        }
    }

@JvmName("FlatVersions_toTreeVersions")
fun FlatVersions.toTreeVersions(): TreeVersions = toMutableTreeVersions().toTreeVersions()

@JvmName("FlatVersions_toMutableTreeVersions")
fun FlatVersions.toMutableTreeVersions(): MutableTreeVersions =
    createMutableTreeVersions {
        this@toMutableTreeVersions.forEach {
            groupTreeVersion(it.key.removePrefix("versions."), it.value)
        }
    }

fun MutableTreeVersions.toTreeVersions(): TreeVersions =
    TreeVersions(
        name = name,
        version = version,
        subTreeVersions = subTreeVersions.toSubTreeVersions(),
    )

private fun SubMutableTreeVersions.toSubTreeVersions(): SubTreeVersions =
    mapValues { (_, value) -> value.toTreeVersions() }.toSortedMap()

private fun createMutableTreeVersions(block: SubMutableTreeVersions.() -> Unit): MutableTreeVersions =
    MutableTreeVersions(
        name = "versions",
        version = null,
        subTreeVersions = SubMutableTreeVersions().apply(block),
    )

private fun SubMutableTreeVersions.groupTreeVersion(key: String, value: Any?): Unit =
    when {
        // For FlatVersions
        key.contains(".") ->
            getOrCreate(key.substringBefore(".")).subTreeVersions.groupTreeVersion(key.substringAfter("."), value)
        else -> when (value) {
            is String -> getOrCreate(key).version = value
            is Map<*, *> ->
                // For TreeVersions
                value.forEach {
                    getOrCreate(key).subTreeVersions.groupTreeVersion(requireNotNull(it.key as? String), it.value)
                }
            else -> Unit
        }
    }

private fun SubMutableTreeVersions.getOrCreate(name: String): MutableTreeVersions =
    getOrPut(name) { MutableTreeVersions(name, null, SubMutableTreeVersions()) }

internal val versionVariableRegex = Regex("\\\$\\{(.*)}")

internal fun FlatVersions.resolveVariable(version: String?): String? =
    version
        ?.let { versionVariableRegex.matchEntire(it) }
        ?.groupValues?.getOrNull(1)
        ?.let { this[it] }
        ?: version
