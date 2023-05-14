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

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeName
import me.omico.elucidator.GENERATED_PACKAGE_NAME
import me.omico.elucidator.GeneratedExtensionFunction
import me.omico.elucidator.GeneratedType
import me.omico.elucidator.generatedExtensionFunctions
import kotlin.reflect.KClass

internal fun FileSpec.Builder.addDslScopeExtensionFunctions(type: GeneratedType): FileSpec.Builder =
    apply {
        generatedExtensionFunctions[type.generatedScopeName]?.forEach { function ->
            addDslScopeExtensionFunction(type.generatedScopeName, function)
        }
    }

private fun FileSpec.Builder.addDslScopeExtensionFunction(
    scope: String,
    function: GeneratedExtensionFunction,
): FileSpec.Builder =
    FunSpec.builder(function.name)
        .receiver(ClassName(GENERATED_PACKAGE_NAME, scope))
        .addParameters(function)
        .addStatement(function)
        .build()
        .let(::addFunction)

private fun FunSpec.Builder.addParameters(function: GeneratedExtensionFunction): FunSpec.Builder =
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

private fun FunSpec.Builder.addStatement(function: GeneratedExtensionFunction): FunSpec.Builder =
    apply {
        val parameters = function.parameters.keys.joinToString(", ") { name ->
            when {
                isVariableArray(name) -> "${actualName(name)} = ${actualName(name)}"
                else -> "$name = $name"
            }
        }
        addStatement("builder.${function.name}($parameters)")
    }

private val isVariableArray: (String) -> Boolean = { it.startsWith("vararg ") }
private val actualName: (String) -> String = { it.removePrefix("vararg ") }
