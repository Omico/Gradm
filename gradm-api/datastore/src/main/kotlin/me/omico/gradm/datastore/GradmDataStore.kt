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

import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import me.omico.gradm.datastore.internal.GradmLocalConfigurationUpdater
import me.omico.gradm.datastore.internal.GradmMetadataUpdater
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.createFile
import kotlin.io.path.deleteIfExists
import kotlin.io.path.exists
import kotlin.io.path.readBytes
import kotlin.io.path.writeBytes

object GradmDataStore {
    private lateinit var _localConfigurationFile: Path
    private lateinit var _metadataFile: Path
    private lateinit var _localConfiguration: GradmLocalConfiguration
    private lateinit var _metadata: GradmMetadata

    val localConfiguration: GradmLocalConfiguration
        get() = run {
            requireInitialized()
            _localConfiguration
        }

    val metadata: GradmMetadata
        get() = run {
            requireInitialized()
            _metadata
        }

    fun load(localConfigurationFile: Path, metadataFile: Path) {
        _localConfigurationFile = localConfigurationFile
        _metadataFile = metadataFile
        _localConfiguration = localConfigurationFile.load()
        _metadata = metadataFile.load()
    }

    fun updateLocalConfiguration(updater: GradmLocalConfigurationScope.() -> Unit): Unit =
        GradmLocalConfigurationUpdater().apply(updater).update()

    fun updateMetadata(metadataUpdater: GradmMetadataScope.() -> Unit): Unit =
        GradmMetadataUpdater().apply(metadataUpdater).update()

    internal fun updateLocalConfiguration(localConfiguration: GradmLocalConfiguration) {
        requireInitialized()
        _localConfiguration = localConfiguration
        _localConfigurationFile.save(localConfiguration)
    }

    internal fun updateMetadata(metadata: GradmMetadata) {
        requireInitialized()
        _metadata = metadata
        _metadataFile.save(metadata)
    }

    private val isInitialized: Boolean
        get() = ::_localConfigurationFile.isInitialized && ::_metadataFile.isInitialized

    private fun requireInitialized(): Unit = require(isInitialized) { "GradmDataStore is not initialized." }

    private inline fun <reified T> Path.load(): T = run {
        parent.createDirectories()
        // Try to load data from file, if failed, delete the file and create a new one.
        val data = runCatching { ProtoBuf.decodeFromByteArray<T>(readBytes()) }.getOrNull()
        if (data != null) return@run data
        deleteIfExists()
        if (!exists()) createFile()
        ProtoBuf.decodeFromByteArray<T>(readBytes())
    }

    private inline fun <reified T> Path.save(data: T) = ProtoBuf.encodeToByteArray(data).let(::writeBytes)
}
