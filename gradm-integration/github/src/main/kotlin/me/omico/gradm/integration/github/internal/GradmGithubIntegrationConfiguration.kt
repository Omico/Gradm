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
package me.omico.gradm.integration.github.internal

import me.omico.gradm.internal.YamlArray
import me.omico.gradm.internal.YamlDocument
import me.omico.gradm.internal.YamlObject
import me.omico.gradm.internal.find
import me.omico.gradm.internal.require

internal typealias Github = YamlObject
internal typealias GithubVersions = YamlArray
internal typealias GithubVersion = YamlObject

internal inline val YamlDocument.github: Github?
    get() = find("github")

internal inline val Github.versions: GithubVersions
    get() = require("versions")

internal inline val GithubVersion.repository: String
    get() = require("repository")

internal inline val GithubVersion.alias: String?
    get() = find("alias")

internal inline val GithubVersion.regex: String?
    get() = find("regex")

internal inline val GithubVersion.group: Int
    get() = find("group", 1)
