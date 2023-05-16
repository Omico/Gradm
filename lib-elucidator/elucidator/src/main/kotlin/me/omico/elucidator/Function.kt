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

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier

public fun function(name: String, block: FunctionScope.() -> Unit): FunSpec =
    FunSpec.builder(name = name).applyDslBuilder(block).build()

public fun constructorFunction(block: FunctionScope.() -> Unit): FunSpec =
    FunSpec.constructorBuilder().applyDslBuilder(block).build()

public fun getterFunction(block: FunctionScope.() -> Unit): FunSpec =
    FunSpec.getterBuilder().applyDslBuilder(block).build()

public fun setterFunction(block: FunctionScope.() -> Unit): FunSpec =
    FunSpec.setterBuilder().applyDslBuilder(block).build()

public fun FunctionScope.clearModifiers(): Unit = builder.modifiers.clear()

public fun FunctionScope.modifier(modifier: KModifier): Unit = modifiers(modifier)

public fun FunctionScope.modifiers(vararg modifiers: KModifier) {
    clearModifiers()
    addModifiers(modifiers = modifiers)
}

public fun FunctionScope.modifiers(modifiers: Iterable<KModifier>) {
    clearModifiers()
    addModifiers(modifiers = modifiers)
}

public inline fun <reified T : Annotation> FunctionScope.addAnnotation(noinline block: AnnotationScope.() -> Unit): Unit =
    annotation<T>(block = block).let(::addAnnotation)

public inline fun <reified T> FunctionScope.addParameter(name: String, vararg modifiers: KModifier) {
    builder.addParameter(name = name, type = T::class, modifiers = modifiers)
}

public inline fun <reified T> FunctionScope.addParameter(name: String, modifiers: Iterable<KModifier>) {
    builder.addParameter(name = name, type = T::class, modifiers = modifiers)
}

public inline fun <reified T> FunctionScope.returnType(kdoc: CodeBlock = EmptyCodeBlock) {
    builder.returns(returnType = T::class, kdoc = kdoc)
}

public fun FunctionScope.returnStatement(format: String, vararg args: Any): Unit =
    addStatement(format = "return $format", args = args)

public inline fun <reified T> FunctionScope.returnStatement(
    format: String,
    vararg args: Any,
    kdoc: CodeBlock = EmptyCodeBlock,
) {
    returnStatement(format = format, args = args)
    returnType<T>(kdoc = kdoc)
}
