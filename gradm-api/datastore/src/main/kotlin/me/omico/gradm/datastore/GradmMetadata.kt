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
package me.omico.gradm.datastore

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import me.omico.gradm.datastore.maven.MavenMetadata

@Serializable
data class GradmMetadata(
    @ProtoNumber(1) val timestamp: Long = System.currentTimeMillis(),
    @ProtoNumber(2) val maven: List<MavenMetadata> = listOf(),
)
