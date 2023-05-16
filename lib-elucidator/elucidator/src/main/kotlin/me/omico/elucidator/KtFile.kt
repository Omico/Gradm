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

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
import java.nio.file.Path

public fun ktFile(
    packageName: String = "",
    fileName: String,
    block: KtFileScope.() -> Unit,
): FileSpec =
    FileSpec.builder(packageName = packageName, fileName = fileName).applyDslBuilder(block).build()

public fun KtFileScope.addFunction(name: String, block: FunctionScope.() -> Unit): Unit =
    function(name = name, block = block).let(::addFunction)

public fun KtFileScope.addObjectType(name: String, block: TypeScope.() -> Unit): Unit =
    objectType(name = name, block = block).let(::addType)

public fun KtFileScope.addClassType(name: String, block: TypeScope.() -> Unit): Unit =
    classType(name = name, block = block).let(::addType)

public inline fun <reified T : Annotation> KtFileScope.addAnnotation(noinline block: AnnotationScope.() -> Unit): Unit =
    annotation(type = T::class, block = block).let(::addAnnotation)

public inline fun <reified T> KtFileScope.addProperty(
    name: String,
    vararg modifiers: KModifier,
    noinline block: PropertyScope.() -> Unit,
): Unit =
    property<T>(name = name, modifiers = modifiers, block = block).let(::addProperty)

public fun KtFileScope.writeTo(path: Path): Unit = build().writeTo(path)
