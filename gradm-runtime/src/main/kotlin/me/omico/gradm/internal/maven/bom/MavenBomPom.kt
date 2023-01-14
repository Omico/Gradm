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
package me.omico.gradm.internal.maven.bom

import me.omico.gradm.internal.maven.documentBuilder
import me.omico.gradm.internal.maven.forEach
import me.omico.gradm.internal.maven.map
import org.w3c.dom.Document
import org.w3c.dom.Node
import java.nio.file.Path
import kotlin.io.path.absolutePathString

data class MavenBomPom(
    val group: String,
    val artifact: String,
    val version: String,
    val dependencies: List<MavenBomDependency>,
) {
    val module: String by lazy { "$group:$artifact" }
}

data class MavenBomDependency(
    val group: String,
    val artifact: String,
    val version: String,
) {
    val module: String by lazy { "$group:$artifact" }
}

internal fun MavenBomPom(path: Path): MavenBomPom =
    documentBuilder.parse(path.absolutePathString()).let(Document::MavenBomPom)

private fun Document.MavenBomPom(): MavenBomPom =
    MavenBomPom(
        group = getElementsByTagName("groupId").item(0).textContent,
        artifact = getElementsByTagName("artifactId").item(0).textContent,
        version = getElementsByTagName("version").item(0).textContent,
        dependencies = getElementsByTagName("dependency").map(Node::MavenBomDependency),
    )

private fun Node.MavenBomDependency(): MavenBomDependency = run {
    var group: String? = null
    var artifact: String? = null
    var version: String? = null
    childNodes.forEach {
        when (it.nodeName) {
            "groupId" -> group = it.textContent
            "artifactId" -> artifact = it.textContent
            "version" -> version = it.textContent
        }
    }
    MavenBomDependency(
        group = group!!,
        artifact = artifact!!,
        version = version!!,
    )
}
