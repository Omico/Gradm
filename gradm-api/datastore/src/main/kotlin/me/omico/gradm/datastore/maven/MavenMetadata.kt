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
package me.omico.gradm.datastore.maven

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
data class MavenMetadata(
    @ProtoNumber(1) val uri: String,
    @ProtoNumber(2) val repository: String,
    @ProtoNumber(3) val groupId: String,
    @ProtoNumber(4) val artifactId: String,
    @ProtoNumber(5) val latestVersion: String,
    @ProtoNumber(6) val versions: List<String>,
    @ProtoNumber(7) val lastUpdated: Long,
)

inline val MavenMetadata.module: String
    get() = "$groupId:$artifactId"
