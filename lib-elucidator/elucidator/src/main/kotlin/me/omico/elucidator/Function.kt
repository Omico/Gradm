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
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier

fun function(name: String, block: FunctionScope.() -> Unit): FunSpec =
    FunSpec.builder(name).let(::FunctionBuilder).apply(block).build()

fun constructorFunction(block: FunctionScope.() -> Unit): FunSpec =
    FunSpec.constructorBuilder().applyDslBuilder(block).build()

fun getterFunction(block: FunctionScope.() -> Unit): FunSpec =
    FunSpec.getterBuilder().applyDslBuilder(block).build()

fun setterFunction(block: FunctionScope.() -> Unit): FunSpec =
    FunSpec.setterBuilder().applyDslBuilder(block).build()

fun FunctionScope.addModifiers(vararg modifiers: KModifier) {
    builder.addModifiers(*modifiers)
}

fun FunctionScope.addModifiers(modifiers: Iterable<KModifier>) {
    builder.addModifiers(modifiers)
}

fun FunctionScope.clearModifiers() = builder.modifiers.clear()

fun FunctionScope.modifier(modifier: KModifier) = modifiers(modifier)

fun FunctionScope.modifiers(vararg modifiers: KModifier) {
    clearModifiers()
    addModifiers(*modifiers)
}

fun FunctionScope.modifiers(modifiers: Iterable<KModifier>) {
    clearModifiers()
    addModifiers(modifiers)
}

fun FunctionScope.addAnnotation(annotation: AnnotationSpec) {
    builder.addAnnotation(annotation)
}

inline fun <reified T : Annotation> FunctionScope.addAnnotation(noinline block: AnnotationScope.() -> Unit): Unit =
    annotation<T>(block).let(::addAnnotation)

inline fun <reified T> FunctionScope.addParameter(name: String, vararg modifiers: KModifier) {
    builder.addParameter(name, T::class, *modifiers)
}

inline fun <reified T> FunctionScope.addParameter(name: String, modifiers: Iterable<KModifier>) {
    builder.addParameter(name, T::class, modifiers)
}

fun FunctionScope.addStatement(format: String, vararg args: Any) {
    builder.addStatement(format, *args)
}

inline fun <reified T> FunctionScope.returnType(kdoc: CodeBlock = EmptyCodeBlock) {
    builder.returns(T::class, kdoc)
}

fun FunctionScope.returnStatement(format: String, vararg args: Any) = addStatement("return $format", *args)

inline fun <reified T> FunctionScope.returnStatement(
    format: String,
    kdoc: CodeBlock = EmptyCodeBlock,
    vararg args: Any,
) {
    returnStatement(format, *args)
    returnType<T>(kdoc)
}
