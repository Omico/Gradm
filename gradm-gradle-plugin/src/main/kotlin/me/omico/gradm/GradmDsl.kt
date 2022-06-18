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

package me.omico.gradm

import org.gradle.api.initialization.Settings
import org.gradle.kotlin.dsl.configure

fun Settings.gradm(block: GradmExtension.() -> Unit) = extensions.configure(block)

fun GradmExtension.configs(block: GradmConfigs.() -> Unit) = GradmConfigs.let(block)

fun GradmConfigs.development(block: GradmDevelopmentConfigs.() -> Unit) = GradmDevelopmentConfigs.let(block)
