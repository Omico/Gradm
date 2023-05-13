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

internal val types: List<Type> = listOf(
    Type("Annotation", "com.squareup.kotlinpoet.AnnotationSpec", "com.squareup.kotlinpoet.AnnotationSpec.Builder"),
    Type("CodeBlock", "com.squareup.kotlinpoet.CodeBlock", "com.squareup.kotlinpoet.CodeBlock.Builder"),
    Type("KtFile", "com.squareup.kotlinpoet.FileSpec", "com.squareup.kotlinpoet.FileSpec.Builder"),
    Type("Function", "com.squareup.kotlinpoet.FunSpec", "com.squareup.kotlinpoet.FunSpec.Builder"),
    Type("Type", "com.squareup.kotlinpoet.TypeSpec", "com.squareup.kotlinpoet.TypeSpec.Builder"),
    Type("Property", "com.squareup.kotlinpoet.PropertySpec", "com.squareup.kotlinpoet.PropertySpec.Builder"),
)
