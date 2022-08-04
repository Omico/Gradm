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
@file:Suppress(
    "UnusedReceiverParameter",
    "unused",
)

package org.gradle.kotlin.dsl

import me.omico.gradm.GradmConfigs
import me.omico.gradm.GradmDevelopmentConfigs
import me.omico.gradm.GradmExtension
import org.gradle.api.Action
import org.gradle.api.initialization.Settings

val Settings.gradm: GradmExtension
    get() = extensions.getByType()

fun Settings.gradm(configure: Action<GradmExtension>) = extensions.configure("gradm", configure)

val GradmExtension.configs: GradmConfigs
    get() = GradmConfigs

fun GradmExtension.configs(block: GradmConfigs.() -> Unit) = GradmConfigs.let(block)

val GradmExtension.development: GradmDevelopmentConfigs
    get() = GradmDevelopmentConfigs

fun GradmConfigs.development(block: GradmDevelopmentConfigs.() -> Unit) = GradmDevelopmentConfigs.let(block)
