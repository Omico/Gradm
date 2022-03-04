package me.omico.gradm.internal.codegen

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec

internal data class Library(
    val module: String,
    val artifact: String,
    val version: String,
)

internal fun TypeSpec.Builder.addLibrary(meta: Library): TypeSpec.Builder =
    apply {
        PropertySpec.builder(meta.artifact, String::class)
            .initializer("${meta.artifact}(\"${meta.version}\")")
            .build()
            .also(::addProperty)
        FunSpec.builder(meta.artifact)
            .addParameter("version", String::class)
            .returns(String::class)
            .addStatement("return \"${meta.module}:\$version\"", String::class)
            .build()
            .also(::addFunction)
    }
