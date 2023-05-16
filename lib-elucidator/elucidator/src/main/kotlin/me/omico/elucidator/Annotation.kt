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
package me.omico.elucidator

import com.squareup.kotlinpoet.AnnotationSpec
import kotlin.reflect.KClass

public fun <T : Annotation> annotation(type: KClass<T>, block: AnnotationScope.() -> Unit): AnnotationSpec =
    AnnotationSpec.builder(type = type).applyDslBuilder(block).build()

public inline fun <reified T : Annotation> annotation(noinline block: AnnotationScope.() -> Unit): AnnotationSpec =
    annotation(type = T::class, block = block)
