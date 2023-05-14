/*
 * Copyright 2022-2023 Omico
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

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import me.omico.elucidator.KtFileScope
import me.omico.elucidator.TypeScope
import me.omico.elucidator.addObjectType
import me.omico.elucidator.addProperty
import me.omico.elucidator.applyDslBuilder
import me.omico.elucidator.initializer
import me.omico.elucidator.ktFile
import me.omico.elucidator.modifier
import me.omico.gradm.GRADM_PACKAGE_NAME
import me.omico.gradm.integration.applyGradmIntegrations
import me.omico.gradm.internal.config.TreeVersions
import me.omico.gradm.internal.config.toTreeVersions

internal fun CodeGenerator.generateVersionsSourceFile() =
    mutableMapOf<String, String>()
        .apply { putAll(flatVersions) }
        .apply(gradmProjectPaths::applyGradmIntegrations)
        .toTreeVersions()
        .toFileSpec()
        .writeTo(generatedSourcesDirectory)

private fun TreeVersions.toFileSpec(): FileSpec =
    ktFile(GRADM_PACKAGE_NAME, "Versions") {
        addSuppressWarningTypes()
        addGradmComment()
        addVersionsObjects(this@toFileSpec)
    }

private fun KtFileScope.addVersionsObjects(versions: TreeVersions) =
    addObjectType("Versions") {
        builder.addSubVersionsProperties(versions)
    }

private fun TypeSpec.Builder.addSubVersionsProperties(versions: TreeVersions): TypeSpec.Builder =
    applyDslBuilder {
        versions.subTreeVersions.toSortedMap().forEach { (name, subVersions) ->
            addVersionProperty(name, subVersions)
            addSubVersionsProperty(name, subVersions)
            builder.addSubVersionsObjects(name, subVersions)
        }
    }

private fun TypeScope.addVersionProperty(propertyName: String, subVersions: TreeVersions) {
    if (subVersions.subTreeVersions.isNotEmpty()) return
    val version = subVersions.version ?: return
    addProperty<String>(propertyName) {
        modifier(KModifier.CONST)
        initializer("\"$version\"")
    }
}

private fun TypeScope.addSubVersionsProperty(propertyName: String, subVersions: TreeVersions) {
    if (subVersions.subTreeVersions.isEmpty()) return
    addProperty(propertyName, ClassName("", "${propertyName.capitalize()}Versions")) {
        initializer("${propertyName.capitalize()}Versions")
    }
}

private fun TypeSpec.Builder.addSubVersionsObjects(name: String, subVersions: TreeVersions): TypeSpec.Builder =
    apply {
        if (subVersions.subTreeVersions.isEmpty()) return this
        TypeSpec.objectBuilder("${name.capitalize()}Versions")
            .addSubVersionsProperties(subVersions)
            .addSubVersionsOverrideFunction(subVersions)
            .build()
            .also(::addType)
    }

private fun TypeSpec.Builder.addSubVersionsOverrideFunction(subVersions: TreeVersions): TypeSpec.Builder =
    apply {
        val version = subVersions.version ?: return this
        FunSpec.builder("toString")
            .addModifiers(KModifier.OVERRIDE)
            .returns(String::class)
            .addStatement("return \"$version\"", String::class)
            .build()
            .also(::addFunction)
    }
