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

import org.junit.jupiter.api.Test

class VersionTest {
    @Test
    fun `1_2_0-alpha01 same as 1_2_0-alpha01`() {
        assert(Version.parse("1.2.0-alpha01") == Version.parse("1.2.0-alpha01"))
    }

    @Test
    fun `1_2_0-alpha01 older than 1_2_0-alpha02`() {
        assert(Version.parse("1.2.0-alpha01") < Version.parse("1.2.0-alpha02"))
    }

    @Test
    fun `1_2_0-rc01 newer than 1_2_0-alpha02`() {
        assert(Version.parse("1.2.0-rc01") > Version.parse("1.2.0-alpha02"))
    }

    @Test
    fun `1_2_0-SNAPSHOT older than 1_2_0-rc02`() {
        assert(Version.parse("1.2.0-SNAPSHOT") < Version.parse("1.2.0-rc02"))
    }

    @Test
    fun `1_2_0 newer than 1_2_0-SNAPSHOT`() {
        assert(Version.parse("1.2.0") > Version.parse("1.2.0-SNAPSHOT"))
    }
}
