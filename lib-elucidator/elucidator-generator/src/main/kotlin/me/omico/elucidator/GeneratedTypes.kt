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

import com.squareup.kotlinpoet.ClassName

internal val generatedTypes: List<GeneratedType> = listOf(
    GeneratedType("Annotation", "com.squareup.kotlinpoet.AnnotationSpec"),
    GeneratedType("CodeBlock", "com.squareup.kotlinpoet.CodeBlock"),
    GeneratedType("Function", "com.squareup.kotlinpoet.FunSpec"),
    GeneratedType("KtFile", "com.squareup.kotlinpoet.FileSpec"),
    GeneratedType("Property", "com.squareup.kotlinpoet.PropertySpec"),
    GeneratedType("Type", "com.squareup.kotlinpoet.TypeSpec"),
)

internal data class GeneratedType(
    val name: String,
    val objectClass: String,
    val builderClass: String = "$objectClass.Builder",
) {
    val objectClassName: ClassName = ClassName.bestGuess(objectClass)
    val builderClassName: ClassName = ClassName.bestGuess(builderClass)

    val generatedFileName: String = "$name.generated"

    val generatedScopeName: String = "${name}Scope"
    val generatedScopeClassName: ClassName = ClassName(GENERATED_PACKAGE_NAME, generatedScopeName)

    val generatedBuilderName: String = "${name}Builder"
}
