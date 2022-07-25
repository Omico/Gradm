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
package me.omico.gradm

import java.nio.file.Path

interface GradmConfigs {
    var debug: Boolean
    var format: Boolean
    var indent: Int

    companion object : GradmConfigs {
        lateinit var rootDir: Path
        var offline: Boolean = false
        var requireRefresh: Boolean = false

        override var debug: Boolean = false
        override var format: Boolean = false
        override var indent: Int = 2
    }
}

interface GradmDevelopmentConfigs {
    var customGradleBuildScript: String?
    var customGradleSettingsScript: String?

    companion object : GradmDevelopmentConfigs {
        override var customGradleBuildScript: String? = null
        override var customGradleSettingsScript: String? = null
    }
}
