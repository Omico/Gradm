package me.omico.elucidator.type

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import me.omico.elucidator.GeneratedType

internal fun FileSpec.Builder.addDslScopeInterface(type: GeneratedType): FileSpec.Builder =
    TypeSpec.interfaceBuilder(type.generatedScopeName)
        .addProperty("builder", type.builderClassName)
        .apply {
            FunSpec.builder("build")
                .addModifiers(KModifier.ABSTRACT)
                .returns(type.objectClassName)
                .build()
                .let(::addFunction)
        }
        .build()
        .let(::addType)
