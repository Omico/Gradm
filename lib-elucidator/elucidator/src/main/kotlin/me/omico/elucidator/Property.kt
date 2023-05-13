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

import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName

inline fun <reified T> property(
    name: String,
    vararg modifiers: KModifier,
    noinline block: PropertyScope.() -> Unit,
): PropertySpec =
    PropertySpec.builder(name, T::class, *modifiers).applyDslBuilder(block).build()

fun property(
    name: String,
    type: TypeName,
    vararg modifiers: KModifier,
    block: PropertyScope.() -> Unit,
): PropertySpec =
    PropertySpec.builder(name, type, *modifiers).applyDslBuilder(block).build()

fun PropertyScope.addModifiers(vararg modifiers: KModifier) {
    builder.addModifiers(*modifiers)
}

fun PropertyScope.clearModifiers() = builder.modifiers.clear()

fun PropertyScope.modifier(modifier: KModifier) = modifiers(modifier)

fun PropertyScope.modifiers(vararg modifiers: KModifier) {
    clearModifiers()
    addModifiers(*modifiers)
}

fun PropertyScope.initializer(format: String, vararg args: Any?) {
    builder.initializer(format, *args)
}
