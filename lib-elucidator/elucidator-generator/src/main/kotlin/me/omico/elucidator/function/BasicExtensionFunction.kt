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
package me.omico.elucidator.function

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asTypeName
import me.omico.elucidator.GENERATED_PACKAGE_NAME
import me.omico.elucidator.GeneratedType
import kotlin.reflect.KClass

internal fun FileSpec.Builder.addDslScopeBasicExtensionFunctions(type: GeneratedType): FileSpec.Builder =
    apply {
        basicExtensionFunctions[type.generatedScopeName]?.forEach { function ->
            addDslScopeBasicExtensionFunction(type.generatedScopeName, function)
        }
    }

private fun FileSpec.Builder.addDslScopeBasicExtensionFunction(
    scope: String,
    function: BasicExtensionFunction,
): FileSpec.Builder =
    FunSpec.builder(function.name)
        .receiver(ClassName(GENERATED_PACKAGE_NAME, scope))
        .addParameters(function)
        .addStatement(function)
        .build()
        .let(::addFunction)

private fun FunSpec.Builder.addParameters(function: BasicExtensionFunction): FunSpec.Builder =
    apply { function.parameters.forEach { (name, type) -> addParameter(name, type) } }

private fun FunSpec.Builder.addParameter(name: String, type: Any): FunSpec.Builder =
    apply {
        val builder = when (type) {
            is KClass<*> -> when {
                isVariableArray(name) -> ParameterSpec.builder(actualName(name), type, KModifier.VARARG)
                else -> ParameterSpec.builder(name, type)
            }
            is TypeName -> when {
                isVariableArray(name) -> ParameterSpec.builder(actualName(name), type, KModifier.VARARG)
                else -> ParameterSpec.builder(name, type)
            }
            else -> return@apply
        }
        builder.build().let(::addParameter)
    }

private fun FunSpec.Builder.addStatement(function: BasicExtensionFunction): FunSpec.Builder =
    apply {
        val parameters = function.parameters.keys.joinToString(", ") { name ->
            when {
                isVariableArray(name) -> "${actualName(name)} = ${actualName(name)}"
                else -> "$name = $name"
            }
        }
        addStatement("builder.${function.name}($parameters)")
    }

private data class BasicExtensionFunction(
    val name: String,
    val parameters: Map<String, Any>,
)

private typealias BasicExtensionFunctions = Map<String, List<BasicExtensionFunction>>

private fun BasicExtensionFunction(
    name: String,
    vararg parameters: Pair<String, Any>,
): BasicExtensionFunction =
    BasicExtensionFunction(name, mapOf(*parameters))

private val basicExtensionFunctions: BasicExtensionFunctions by lazy {
    mapOf(
        "AnnotationScope" to BasicExtensionFunctions_AnnotationScope,
        "FunctionScope" to BasicExtensionFunctions_FunctionScope,
        "KtFileScope" to BasicExtensionFunctions_KtFileScope,
        "PropertyScope" to BasicExtensionFunctions_PropertyScope,
        "TypeScope" to BasicExtensionFunctions_TypeScope,
    )
}

private val BasicExtensionFunctions_AnnotationScope: List<BasicExtensionFunction> by lazy {
    buildList {
        add(BasicExtensionFunction_addMember)
    }
}

private val BasicExtensionFunctions_FunctionScope: List<BasicExtensionFunction> by lazy {
    buildList {
        addAll(BasicExtensionFunctions_addModifiers)
        addAll(BasicExtensionFunctions_controlFlow)
        add(BasicExtensionFunction_addAnnotation)
        add(BasicExtensionFunction_addComment)
        add(BasicExtensionFunction_addStatement)
    }
}

private val BasicExtensionFunctions_KtFileScope: List<BasicExtensionFunction> by lazy {
    buildList {
        add(BasicExtensionFunction_addAnnotation)
        add(BasicExtensionFunction_addFileComment)
        add(BasicExtensionFunction_addFunction)
        add(BasicExtensionFunction_addProperty)
        add(BasicExtensionFunction_addType)
    }
}

private val BasicExtensionFunctions_PropertyScope: List<BasicExtensionFunction> by lazy {
    buildList {
        add(BasicExtensionFunction_addAnnotation)
        addAll(BasicExtensionFunctions_addModifiers)
        listOf(
            BasicExtensionFunction(
                name = "initializer",
                parameters = arrayOf(
                    "format" to String::class,
                    "vararg args" to Any::class.asTypeName().copy(nullable = true),
                ),
            ),
        ).let(::addAll)
    }
}

private val BasicExtensionFunctions_TypeScope: List<BasicExtensionFunction> by lazy {
    buildList {
        add(BasicExtensionFunction_addAnnotation)
        add(BasicExtensionFunction_addFunction)
        add(BasicExtensionFunction_addProperty)
        add(BasicExtensionFunction_addSuperclassConstructorParameter)
        add(BasicExtensionFunction_addType)
    }
}

private val BasicExtensionFunction_addAnnotation: BasicExtensionFunction =
    BasicExtensionFunction("addAnnotation", "annotationSpec" to AnnotationSpec::class)

private val BasicExtensionFunctions_addModifiers: List<BasicExtensionFunction> = listOf(
    BasicExtensionFunction("addModifiers", "modifiers" to Iterable::class.parameterizedBy(KModifier::class)),
    BasicExtensionFunction("addModifiers", "vararg modifiers" to KModifier::class),
)

private val BasicExtensionFunction_addStatement: BasicExtensionFunction =
    BasicExtensionFunction("addStatement", "format" to String::class, "vararg args" to Any::class)

private val BasicExtensionFunction_addFunction: BasicExtensionFunction =
    BasicExtensionFunction("addFunction", "funSpec" to FunSpec::class)

private val BasicExtensionFunction_addProperty: BasicExtensionFunction =
    BasicExtensionFunction("addProperty", "propertySpec" to PropertySpec::class)

private val BasicExtensionFunction_addType: BasicExtensionFunction =
    BasicExtensionFunction("addType", "typeSpec" to TypeSpec::class)

private val BasicExtensionFunction_addSuperclassConstructorParameter: BasicExtensionFunction =
    BasicExtensionFunction(
        "addSuperclassConstructorParameter",
        "format" to String::class,
        "vararg args" to Any::class,
    )

private val BasicExtensionFunction_addFileComment: BasicExtensionFunction =
    BasicExtensionFunction("addFileComment", "format" to String::class, "vararg args" to Any::class)

private val BasicExtensionFunction_addComment: BasicExtensionFunction =
    BasicExtensionFunction("addComment", "format" to String::class, "vararg args" to Any::class)

private val BasicExtensionFunction_addMember: BasicExtensionFunction =
    BasicExtensionFunction("addMember", "format" to String::class, "vararg args" to Any::class)

private val BasicExtensionFunctions_controlFlow: List<BasicExtensionFunction> = listOf(
    BasicExtensionFunction("beginControlFlow", "controlFlow" to String::class, "vararg args" to Any::class),
    BasicExtensionFunction("nextControlFlow", "controlFlow" to String::class, "vararg args" to Any::class),
    BasicExtensionFunction("endControlFlow"),
)
