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
package me.omico.gradm.internal.maven.bom

import me.omico.gradm.VersionsMeta
import me.omico.gradm.internal.YamlDocument
import me.omico.gradm.internal.config.Dependency
import me.omico.gradm.internal.config.dependencies
import org.gradle.api.Project
import org.gradle.api.artifacts.result.ResolvedArtifactResult
import org.gradle.maven.MavenModule
import org.gradle.maven.MavenPomArtifact

internal fun Project.resolveBomVersionsMeta(document: YamlDocument): VersionsMeta =
    document
        .dependencies
        .filter(Dependency::bom)
        .map(::MavenBomPom)
        .flatMap(MavenBomPom::dependencies)
        .associate { it.module to it.version }

private fun Project.MavenBomPom(dependency: Dependency): MavenBomPom =
    dependencies
        .createArtifactResolutionQuery()
        .forModule(dependency.group, dependency.artifact, dependency.version!!)
        .withArtifacts(MavenModule::class.java, MavenPomArtifact::class.java)
        .execute()
        .resolvedComponents
        .flatMap { component ->
            component.getArtifacts(MavenPomArtifact::class.java)
                .filterIsInstance<ResolvedArtifactResult>()
                .map(ResolvedArtifactResult::getFile)
        }
        .first()
        .toPath()
        .let(::MavenBomPom)
