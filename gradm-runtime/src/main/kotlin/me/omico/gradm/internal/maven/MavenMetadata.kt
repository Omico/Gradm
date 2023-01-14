/*
 * Copyright 2022-2023 Omico
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

import java.nio.file.Path
import kotlin.io.path.absolutePathString

data class MavenMetadata(
    val group: String,
    val artifact: String,
    val latestVersion: String,
    val versions: List<String>,
) {
    val module: String by lazy { "$group:$artifact" }
}

internal fun MavenMetadata(metadataPath: Path): MavenMetadata =
    documentBuilder.parse(metadataPath.absolutePathString())
        .let { document ->
            MavenMetadata(
                group = document.getElementsByTagName("groupId").item(0).textContent,
                artifact = document.getElementsByTagName("artifactId").item(0).textContent,
                latestVersion = document.getElementsByTagName("latest").item(0).textContent,
                versions = document.getElementsByTagName("versions").item(0).childNodes
                    .mapNotNull { if (it.nodeName == "version") it.textContent else null },
            )
        }
