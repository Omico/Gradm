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

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.UNIT
import me.omico.elucidator.GeneratedType

internal fun FileSpec.Builder.addInteroperabilityFunction(type: GeneratedType): FileSpec.Builder =
    FunSpec.builder("applyDslBuilder")
        .receiver(type.builderClassName)
        .apply {
            ParameterSpec
                .builder(
                    name = "builder",
                    type = LambdaTypeName.get(
                        receiver = type.generatedScopeClassName,
                        returnType = UNIT,
                    ),
                )
                .build()
                .let(::addParameter)
        }
        .addStatement("return ${type.generatedBuilderName}(this).apply(builder).builder")
        .returns(type.builderClassName)
        .build()
        .let(::addFunction)
