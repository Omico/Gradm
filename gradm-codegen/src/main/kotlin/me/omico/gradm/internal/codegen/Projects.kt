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
package me.omico.gradm.internal.codegen

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.internal.artifacts.dependencies.ProjectDependencyInternal
import java.nio.file.Path
import java.util.TreeMap

fun generateProjectSourceFiles(
    projectPaths: List<String>,
    generatedSourcesDirectory: Path,
) {
    if (projectPaths.isEmpty()) return
    FileSpec.builder("", "Projects")
        .addSuppressWarningTypes()
        .addGradmComment()
        .addRootProjectStructure("RootProject", projectPaths)
        .build()
        .writeTo(generatedSourcesDirectory)
}

internal data class NestedProject(
    val name: String,
    val path: String? = null,
    val subProjects: NestedProjects = NestedProjects(),
) {
    val propertyName: String by lazy(name::camelCase)

    fun fullName(parentName: String): String =
        when (parentName) {
            Root.name -> name.capitalize()
            else -> "${parentName.capitalize()}${name.capitalize()}"
        }

    fun className(parentName: String): String = "${fullName(parentName)}Project"

    companion object {
        val Root = NestedProject(
            name = "root",
            path = ":",
        )
    }
}

internal typealias NestedProjects = TreeMap<String, NestedProject>

private fun FileSpec.Builder.addRootProjectStructure(
    rootClassName: String,
    projectPaths: List<String>,
): FileSpec.Builder =
    apply {
        val rootNestedProject = NestedProject.Root
            .apply { projectPaths.forEach(::add) }
        classObject(rootNestedProject).let(::addType)
        PropertySpec.builder("gradmProjects", ClassName("", rootClassName))
            .receiver(DependencyHandler::class)
            .getter(
                FunSpec
                    .getterBuilder()
                    .addStatement("return $rootClassName(this)")
                    .build(),
            )
            .build()
            .let(::addProperty)
        FunSpec.builder("project")
            .addModifiers(KModifier.PRIVATE)
            .receiver(DependencyHandler::class)
            .addParameter("path", String::class)
            .addStatement("return project(mapOf(\"path\" to path)) as ProjectDependencyInternal")
            .returns(ProjectDependencyInternal::class)
            .build()
            .let(::addFunction)
    }

// internal for testing
internal fun NestedProject.add(
    fullPath: String,
    splitPaths: List<String> = fullPath.split(":").filter(String::isNotBlank),
) {
    when (splitPaths.size) {
        0 -> return
        1 -> subProjects[splitPaths.first()] = NestedProject(splitPaths.first(), fullPath)
        else -> {
            subProjects
                .getOrPut(splitPaths.first()) { NestedProject(splitPaths.first()) }
                .add(fullPath, splitPaths.drop(1))
        }
    }
}

private fun classObject(
    nestedProject: NestedProject,
    parentName: String = nestedProject.name,
): TypeSpec =
    TypeSpec.classBuilder(nestedProject.className(parentName))
        .apply {
            FunSpec.constructorBuilder()
                .addParameter("dependencies", DependencyHandler::class)
                .build()
                .let(::primaryConstructor)
        }
        .addProjectDependencySuperinterface(nestedProject)
        .apply {
            nestedProject.subProjects.values.forEach { subProject ->
                val subName = nestedProject.fullName(parentName)
                classObject(subProject, subName).let(::addType)
                addProjectProperty(subProject, subName)
            }
        }
        .build()

private fun TypeSpec.Builder.addProjectDependencySuperinterface(nestedProject: NestedProject): TypeSpec.Builder =
    apply {
        if (nestedProject == NestedProject.Root) return@apply
        val path = nestedProject.path ?: return@apply
        addSuperinterface(
            superinterface = ProjectDependencyInternal::class,
            delegate = CodeBlock.of("dependencies.project(\"$path\")"),
        )
        PropertySpec.builder("dependencies", DependencyHandler::class)
            .addModifiers(KModifier.PRIVATE)
            .initializer("dependencies")
            .build()
            .let(::addProperty)
    }

private fun TypeSpec.Builder.addProjectProperty(nestedProject: NestedProject, parentName: String) =
    apply {
        val className = nestedProject.className(parentName)
        PropertySpec.builder(nestedProject.propertyName, ClassName("", className))
            .delegate("lazy { $className(dependencies) }")
            .build()
            .let(::addProperty)
    }
