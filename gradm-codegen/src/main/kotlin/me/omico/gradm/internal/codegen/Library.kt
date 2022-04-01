/*
 * Copyright 2022 Omico
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
package me.omico.gradm.internal.codegen

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import me.omico.gradm.VersionsMeta
import me.omico.gradm.internal.config.Library

internal data class CodegenLibrary(
    val group: String,
    val artifact: String,
    val version: String,
) {
    val module: String by lazy { "$group:$artifact" }
}

internal fun Library.toCodegenLibrary(versionsMeta: VersionsMeta): CodegenLibrary =
    CodegenLibrary(
        group = group,
        artifact = artifact,
        version = version ?: versionsMeta[module]!!
    )

internal fun TypeSpec.Builder.addLibrary(propertyName: String, library: CodegenLibrary?): TypeSpec.Builder =
    apply {
        if (library == null) return@apply
        PropertySpec.builder(propertyName.camelCase(), String::class)
            .initializer("${propertyName.camelCase()}(\"${library.version}\")")
            .build()
            .also(::addProperty)
        FunSpec.builder(propertyName.camelCase())
            .addParameter("version", String::class)
            .returns(String::class)
            .addStatement("return \"${library.group}:${library.artifact}:\$version\"", String::class)
            .build()
            .also(::addFunction)
    }
