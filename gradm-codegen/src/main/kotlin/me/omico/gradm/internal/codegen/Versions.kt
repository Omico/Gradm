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
import com.squareup.kotlinpoet.KModifier
import me.omico.elucidator.KtFileScope
import me.omico.elucidator.TypeScope
import me.omico.elucidator.addFunction
import me.omico.elucidator.addObject
import me.omico.elucidator.addProperty
import me.omico.elucidator.initializer
import me.omico.elucidator.ktFile
import me.omico.elucidator.modifier
import me.omico.elucidator.returnStatement
import me.omico.elucidator.writeTo
import me.omico.gradm.GRADM_PACKAGE_NAME
import me.omico.gradm.integration.GradmIntegrationManager
import me.omico.gradm.internal.config.TreeVersions
import me.omico.gradm.internal.config.toTreeVersions

internal fun CodeGenerator.generateVersionsSourceFile() {
    ktFile(GRADM_PACKAGE_NAME, "Versions") {
        addSuppressWarningTypes(types = defaultSuppressWarningTypes + "ConstPropertyName")
        addGradmComment()
        addVersionsObjects(createTreeVersions())
        writeTo(generatedSourcesDirectory)
    }
}

private fun CodeGenerator.createTreeVersions(): TreeVersions =
    mutableMapOf<String, String>()
        .apply { putAll(flatVersions) }
        .apply { GradmIntegrationManager.generate(gradmProjectPaths, this) }
        .toTreeVersions()

private fun KtFileScope.addVersionsObjects(versions: TreeVersions): Unit =
    addObject("Versions") {
        addSubVersionsProperties(versions)
    }

private fun TypeScope.addSubVersionsProperties(versions: TreeVersions): Unit =
    versions.subTreeVersions.toSortedMap().forEach { (name, subVersions) ->
        addVersionProperty(name, subVersions)
        addSubVersionsProperty(name, subVersions)
        addSubVersionsObjects(name, subVersions)
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

private fun TypeScope.addSubVersionsObjects(name: String, subVersions: TreeVersions) {
    if (subVersions.subTreeVersions.isEmpty()) return
    addObject("${name.capitalize()}Versions") {
        addSubVersionsProperties(subVersions)
        addSubVersionsOverrideFunction(subVersions)
    }
}

private fun TypeScope.addSubVersionsOverrideFunction(subVersions: TreeVersions) {
    val version = subVersions.version ?: return
    addFunction("toString") {
        modifier(KModifier.OVERRIDE)
        returnStatement<String>("\"${version}\"")
    }
}
