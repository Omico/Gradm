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
