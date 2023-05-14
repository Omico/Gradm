package me.omico.elucidator.type

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import me.omico.elucidator.GeneratedType

internal fun FileSpec.Builder.addDslBuilderClass(type: GeneratedType): FileSpec.Builder =
    TypeSpec.classBuilder(type.generatedBuilderName)
        .apply {
            FunSpec.constructorBuilder()
                .addParameter("builder", type.builderClassName)
                .build()
                .let(::primaryConstructor)
        }
        .apply {
            PropertySpec.builder("builder", type.builderClassName)
                .addModifiers(KModifier.OVERRIDE)
                .initializer("builder")
                .build()
                .let(::addProperty)
        }
        .addSuperinterface(type.generatedScopeClassName)
        .apply {
            FunSpec.builder("build")
                .addModifiers(KModifier.OVERRIDE)
                .returns(type.objectClassName)
                .addStatement("return builder.build()")
                .build()
                .let(::addFunction)
        }
        .build()
        .let(::addType)
