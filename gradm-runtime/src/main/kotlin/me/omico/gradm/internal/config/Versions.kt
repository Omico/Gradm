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
