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
package me.omico.gradm.datastore.internal

import me.omico.gradm.datastore.GradmDataStore
import me.omico.gradm.datastore.GradmMetadataScope
import me.omico.gradm.datastore.maven.MavenMetadata

internal class GradmMetadataUpdater : GradmMetadataScope {
    private val newMavenMetadataList = mutableListOf<MavenMetadata>()

    override fun insert(metadata: MavenMetadata) {
        newMavenMetadataList.add(metadata)
    }

    override fun insert(metadata: List<MavenMetadata>) {
        newMavenMetadataList.addAll(metadata)
    }

    fun update() {
        if (newMavenMetadataList.isEmpty()) return
        val mavenUris = newMavenMetadataList.map(MavenMetadata::uri)
        GradmDataStore.metadata.maven.filterNot { it.uri in mavenUris }.let { oldMavenMetadataList ->
            GradmDataStore.metadata.copy(
                timestamp = System.currentTimeMillis(),
                maven = oldMavenMetadataList + newMavenMetadataList,
            ).let(GradmDataStore::updateMetadata)
        }
    }
}
