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

import me.omico.gradm.datastore.maven.MavenMetadata
import me.omico.gradm.test.resources
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path

class GradmDataStoreTest {
    @Test
    fun `test load`(@TempDir tempDir: Path) {
        assertDoesNotThrow {
            GradmDataStore.load(
                localConfigurationFile = tempDir.resolve(".gradm"),
                metadataFile = tempDir.resolve(".metadata"),
            )
        }
    }

    @Test
    fun `test save`(@TempDir tempDir: Path) {
        `test load`(tempDir)
        val testConfigurationPath = resources("gradm.formatted.yml").toString()
        assertDoesNotThrow {
            GradmDataStore.updateLocalConfiguration {
                insertConfigurationPath(testConfigurationPath)
            }
        }
        assert(GradmDataStore.localConfiguration.configurationPaths == setOf(testConfigurationPath))
        val testMavenMetadata = MavenMetadata(
            uri = "test",
            repository = "test",
            groupId = "test",
            artifactId = "test",
            latestVersion = "1.0.0",
            versions = listOf("1.0.0"),
            lastUpdated = 5000,
        )
        assertDoesNotThrow {
            GradmDataStore.updateMetadata {
                insert(testMavenMetadata)
            }
        }
        assert(GradmDataStore.metadata.maven.first() == testMavenMetadata)
        `test load`(tempDir)
        assert(GradmDataStore.metadata.maven.first() == testMavenMetadata)
    }
}
