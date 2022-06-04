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
import me.omico.gradm.internal.require
import java.net.URL
import java.nio.file.Path

val YamlDocument.dependencies: List<Dependency>
    @Suppress("UNCHECKED_CAST")
    get() = run {
        val repositories = repositories
        val versions = versions.toFlatVersions()
        find<YamlObject>("dependencies", emptyMap())
            .flatMap { (repository, groups) ->
                val repositoryUrl = requireNotNull(repositories.find { it.id == repository }?.url) {
                    "Repository $repository not found."
                }
                (groups as YamlObject).flatMap { (group, artifacts) ->
                    (artifacts as YamlObject).map { (artifact, attributes) ->
                        attributes as YamlObject
                        Dependency(
                            repository = repositoryUrl,
                            group = group,
                            artifact = artifact,
                            alias = attributes.require("alias"),
                            version = attributes.find<String>("version").let(versions::resolveVariable),
                        )
                    }
                }
            }
    }

data class Dependency(
    val repository: String,
    val group: String,
    val artifact: String,
    val alias: String,
    val version: String?,
) {
    val module: String by lazy { "$group:$artifact" }
    val metadataUrl: URL by lazy { URL("$repository/${group.replace(".", "/")}/$artifact/maven-metadata.xml") }
}

internal fun Dependency.metadataLocalPath(metadataRootDir: Path): Path =
    metadataRootDir.resolve(group).resolve(artifact).resolve("maven-metadata.xml")
