/*
 * Copyright 2023 Omico
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
package me.omico.gradm.internal.maven

data class Version(
    val major: Int,
    val minor: Int,
    val patch: Int,
    val qualifier: String?,
    val value: String,
) : Comparable<Version> {
    override fun compareTo(other: Version): Int {
        var result = compareValuesBy(this, other, Version::major, Version::minor, Version::patch)
        if (result != 0) return result
        val otherQualifier = other.qualifier
        if (qualifier == otherQualifier) return 0
        if (qualifier == null) return 1
        if (otherQualifier == null) return -1
        val thisQualifierResult = QualifierRegex.matchEntire(qualifier.lowercase())
        val otherQualifierResult = QualifierRegex.matchEntire(otherQualifier.lowercase())
        if (thisQualifierResult?.value == otherQualifierResult?.value) return 0
        if (thisQualifierResult == null) return 1
        if (otherQualifierResult == null) return -1
        result = compareValuesBy(thisQualifierResult.groupValues[1], otherQualifierResult.groupValues[1]) {
            QUALIFIERS.getOrDefault(it, 0)
        }
        if (result != 0) return result
        result = compareValues(
            a = thisQualifierResult.groupValues[2].toInt(),
            b = otherQualifierResult.groupValues[2].toInt(),
        )
        return result
    }

    override fun toString(): String = value

    companion object {
        fun parse(version: String): Version = run {
            val versionSplit = version.split("-", limit = 2)
            val versionNumbers = versionSplit.first().split(".").mapNotNull(String::toIntOrNull)
            Version(
                major = versionNumbers.getOrElse(0) { 0 },
                minor = versionNumbers.getOrElse(1) { 0 },
                patch = versionNumbers.getOrElse(2) { 0 },
                qualifier = versionSplit.getOrNull(1),
                value = version,
            )
        }
    }
}

private val QualifierRegex: Regex = Regex("(alpha|beta|milestone|rc|snapshot)([0-9]{2})?")

private val QUALIFIERS: Map<String, Int> =
    mapOf(
        "snapshot" to -5,
        "alpha" to -4,
        "beta" to -3,
        "milestone" to -2,
        "rc" to -1,
        "" to 0,
    )
