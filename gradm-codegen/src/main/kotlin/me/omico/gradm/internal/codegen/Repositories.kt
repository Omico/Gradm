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

import com.squareup.kotlinpoet.MemberName
import me.omico.elucidator.FunctionScope
import me.omico.elucidator.KtFileScope
import me.omico.elucidator.addFunction
import me.omico.elucidator.addStatement
import me.omico.elucidator.ktFile
import me.omico.elucidator.receiver
import me.omico.elucidator.returnStatement
import me.omico.elucidator.writeTo
import me.omico.gradm.GRADM_PACKAGE_NAME
import me.omico.gradm.internal.YamlDocument
import me.omico.gradm.internal.config.gradleBuildInRepositories
import me.omico.gradm.internal.config.repositories
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.initialization.Settings

internal fun CodeGenerator.generateRepositoriesSourceFile() {
    ktFile(GRADM_PACKAGE_NAME, "Repositories") {
        addSuppressWarningTypes()
        addGradmComment()
        addAddDeclaredRepositoriesForRepositoryHandlerScope(gradmConfigurationDocument)
        addAddDeclaredRepositoriesForSettingsScope()
        addAddDeclaredRepositoriesForProjectScope()
        writeTo(generatedSourcesDirectory)
    }
}

internal fun FunctionScope.addDeclaredRepositoryStatements(document: YamlDocument): Unit =
    document.repositories.forEach { repository ->
        when {
            repository in gradleBuildInRepositories -> addStatement("${repository.id}()")
            !repository.noUpdates -> addStatement("%M(url = %S)", mavenMemberName, repository.url)
        }
    }

private fun KtFileScope.addAddDeclaredRepositoriesForRepositoryHandlerScope(document: YamlDocument): Unit =
    addDeclaredRepositoryStatements<RepositoryHandler> {
        addDeclaredRepositoryStatements(document)
    }

private fun KtFileScope.addAddDeclaredRepositoriesForSettingsScope(): Unit =
    addDeclaredRepositoryStatements<Settings> {
        returnStatement("pluginManagement.repositories.addDeclaredRepositories()")
    }

private fun KtFileScope.addAddDeclaredRepositoriesForProjectScope(): Unit =
    addDeclaredRepositoryStatements<Project> {
        returnStatement("repositories.addDeclaredRepositories()")
    }

private inline fun <reified T> KtFileScope.addDeclaredRepositoryStatements(crossinline block: FunctionScope.() -> Unit): Unit =
    addFunction("addDeclaredRepositories") {
        receiver<T>()
        block()
    }

private val mavenMemberName: MemberName = MemberName("org.gradle.kotlin.dsl", "maven")
