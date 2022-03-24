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

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import me.omico.gradm.internal.YamlDocument
import me.omico.gradm.internal.config.TreeVersions
import me.omico.gradm.internal.config.toTreeVersions
import me.omico.gradm.internal.config.versions
import me.omico.gradm.internal.path.GradmPaths

internal fun generateVersionsSourceFile(document: YamlDocument) {
    document.versions.toTreeVersions().toFileSpec().writeTo(GradmPaths.GeneratedDependenciesProject.sourceDir)
}

private fun TreeVersions.toFileSpec(): FileSpec =
    FileSpec.builder("", "Versions")
        .addSuppressWarningTypes()
        .addGradmComment()
        .addVersionsDslProperty()
        .addVersionsObjects(this)
        .build()

private fun FileSpec.Builder.addVersionsObjects(versions: TreeVersions): FileSpec.Builder =
    apply {
        TypeSpec.objectBuilder("Versions")
            .addSubVersionsProperties(versions)
            .build()
            .also(::addType)
    }

private fun TypeSpec.Builder.addSubVersionsProperties(versions: TreeVersions): TypeSpec.Builder =
    apply {
        versions.subTreeVersions.toSortedMap().forEach { (name, subVersions) ->
            addVersionProperty(name, subVersions.version)
            addSubVersionsProperty(name, subVersions)
            if (subVersions.subTreeVersions.isNotEmpty()) {
                TypeSpec.objectBuilder("${name.capitalize()}Versions")
                    .addSubVersionsProperties(subVersions)
                    .build()
                    .also(::addType)
            }
        }
    }

private fun TypeSpec.Builder.addVersionProperty(propertyName: String, version: String?): TypeSpec.Builder =
    apply {
        if (version == null) return this
        PropertySpec.builder(propertyName, String::class)
            .addModifiers(KModifier.CONST)
            .initializer("\"$version\"")
            .build()
            .also(::addProperty)
    }

private fun TypeSpec.Builder.addSubVersionsProperty(
    propertyName: String,
    subVersions: TreeVersions,
): TypeSpec.Builder =
    apply {
        if (subVersions.subTreeVersions.isEmpty()) return this
        PropertySpec.builder(propertyName, ClassName("", "${propertyName.capitalize()}Versions"))
            .initializer("${propertyName.capitalize()}Versions")
            .build()
            .let(::addProperty)
    }

private fun FileSpec.Builder.addVersionsDslProperty(): FileSpec.Builder =
    apply {
        PropertySpec
            .builder("versions", ClassName("", "Versions"))
            .receiver(ClassName("org.gradle.api", "Project"))
            .getter(FunSpec.getterBuilder().addStatement("return Versions").build())
            .build()
            .also(::addProperty)
    }
