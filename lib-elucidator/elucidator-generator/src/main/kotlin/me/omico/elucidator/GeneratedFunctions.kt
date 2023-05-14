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
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asTypeName

internal typealias GeneratedExtensionFunctions = Map<String, List<GeneratedExtensionFunction>>

internal data class GeneratedExtensionFunction(
    val name: String,
    val parameters: Map<String, Any>,
)

internal fun GeneratedExtensionFunction(
    name: String,
    vararg parameters: Pair<String, Any>,
): GeneratedExtensionFunction =
    GeneratedExtensionFunction(name, mapOf(*parameters))

internal val generatedExtensionFunctions: GeneratedExtensionFunctions by lazy {
    mapOf(
        "AnnotationScope" to GeneratedFunctions_AnnotationScope,
        "FunctionScope" to GeneratedFunctions_FunctionScope,
        "KtFileScope" to GeneratedFunctions_KtFileScope,
        "PropertyScope" to GeneratedFunctions_PropertyScope,
        "TypeScope" to GeneratedFunctions_TypeScope,
    )
}

private val GeneratedFunctions_AnnotationScope: List<GeneratedExtensionFunction> by lazy {
    buildList {
        add(Function_addMember)
    }
}

private val GeneratedFunctions_FunctionScope: List<GeneratedExtensionFunction> by lazy {
    buildList {
        add(Function_addAnnotation)
        addAll(Functions_addModifiers)
        add(Function_addStatement)
    }
}

private val GeneratedFunctions_KtFileScope: List<GeneratedExtensionFunction> by lazy {
    buildList {
        add(Function_addAnnotation)
        add(Function_addFunction)
        add(Function_addProperty)
        add(Function_addType)
        add(Function_addFileComment)
    }
}

private val GeneratedFunctions_PropertyScope: List<GeneratedExtensionFunction> by lazy {
    buildList {
        add(Function_addAnnotation)
        addAll(Functions_addModifiers)
        listOf(
            GeneratedExtensionFunction(
                name = "initializer",
                parameters = arrayOf(
                    "format" to String::class,
                    "vararg args" to Any::class.asTypeName().copy(nullable = true),
                ),
            ),
        ).let(::addAll)
    }
}

private val GeneratedFunctions_TypeScope: List<GeneratedExtensionFunction> by lazy {
    buildList {
        add(Function_addAnnotation)
        add(Function_addFunction)
        add(Function_addProperty)
        add(Function_addType)
        add(Function_addSuperclassConstructorParameter)
    }
}

private val Function_addAnnotation: GeneratedExtensionFunction =
    GeneratedExtensionFunction("addAnnotation", "annotationSpec" to AnnotationSpec::class)

private val Functions_addModifiers: List<GeneratedExtensionFunction> = listOf(
    GeneratedExtensionFunction("addModifiers", "modifiers" to Iterable::class.parameterizedBy(KModifier::class)),
    GeneratedExtensionFunction("addModifiers", "vararg modifiers" to KModifier::class),
)

private val Function_addStatement: GeneratedExtensionFunction =
    GeneratedExtensionFunction("addStatement", "format" to String::class, "vararg args" to Any::class)

private val Function_addFunction: GeneratedExtensionFunction =
    GeneratedExtensionFunction("addFunction", "funSpec" to FunSpec::class)

private val Function_addProperty: GeneratedExtensionFunction =
    GeneratedExtensionFunction("addProperty", "propertySpec" to PropertySpec::class)

private val Function_addType: GeneratedExtensionFunction =
    GeneratedExtensionFunction("addType", "typeSpec" to TypeSpec::class)

private val Function_addSuperclassConstructorParameter: GeneratedExtensionFunction =
    GeneratedExtensionFunction(
        "addSuperclassConstructorParameter",
        "format" to String::class,
        "vararg args" to Any::class,
    )

private val Function_addFileComment: GeneratedExtensionFunction =
    GeneratedExtensionFunction("addFileComment", "format" to String::class, "vararg args" to Any::class)

private val Function_addMember: GeneratedExtensionFunction =
    GeneratedExtensionFunction("addMember", "format" to String::class, "vararg args" to Any::class)
