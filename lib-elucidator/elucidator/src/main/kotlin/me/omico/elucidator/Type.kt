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
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec

fun objectType(name: String, builder: TypeScope.() -> Unit): TypeSpec =
    TypeSpec.objectBuilder(name).applyDslBuilder(builder).build()

inline fun <reified T> TypeScope.superclass() {
    builder.superclass(T::class)
}

fun TypeScope.addSuperclassConstructorParameter(format: String, vararg args: Any) {
    builder.addSuperclassConstructorParameter(format, *args)
}

fun TypeScope.addFunction(name: String, block: FunctionScope.() -> Unit) {
    function(name, block).let(builder::addFunction)
}

inline fun <reified T> TypeScope.addProperty(
    name: String,
    vararg modifiers: KModifier,
    noinline block: PropertyScope.() -> Unit,
) {
    property<T>(name = name, modifiers = modifiers, block = block).let(builder::addProperty)
}

fun TypeScope.addProperty(
    name: String,
    type: TypeName,
    vararg modifiers: KModifier,
    block: PropertyScope.() -> Unit,
) {
    property(name = name, type = type, modifiers = modifiers, block = block).let(builder::addProperty)
}

fun TypeScope.addType(typeSpec: TypeSpec) {
    builder.addType(typeSpec)
}

fun TypeScope.addObjectType(name: String, block: TypeScope.() -> Unit): Unit = objectType(name, block).let(::addType)
