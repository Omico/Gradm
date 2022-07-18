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
@file:Suppress("DEPRECATION")

package me.omico.gradm.internal.gradle.impl

import groovy.lang.Closure
import org.gradle.api.Action
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.dsl.ComponentMetadataHandler
import org.gradle.api.artifacts.dsl.ComponentModuleMetadataHandler
import org.gradle.api.artifacts.dsl.DependencyConstraintHandler
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.artifacts.dsl.ExternalModuleDependencyVariantSpec
import org.gradle.api.artifacts.query.ArtifactResolutionQuery
import org.gradle.api.artifacts.transform.TransformAction
import org.gradle.api.artifacts.transform.TransformParameters
import org.gradle.api.artifacts.transform.TransformSpec
import org.gradle.api.artifacts.transform.VariantTransform
import org.gradle.api.artifacts.type.ArtifactTypeContainer
import org.gradle.api.attributes.AttributesSchema
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderConvertible

object DependencyHandlerImpl : DependencyHandler {

    override fun getExtensions(): ExtensionContainer {
        TODO("Not yet implemented")
    }

    override fun add(configurationName: String, dependencyNotation: Any): Dependency? {
        TODO("Not yet implemented")
    }

    override fun add(configurationName: String, dependencyNotation: Any, configureClosure: Closure<*>): Dependency {
        TODO("Not yet implemented")
    }

    override fun <T : Any?, U : ExternalModuleDependency?> addProvider(
        configurationName: String,
        dependencyNotation: Provider<T>,
        configuration: Action<in U>,
    ) {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> addProvider(configurationName: String, dependencyNotation: Provider<T>) {
        TODO("Not yet implemented")
    }

    override fun <T : Any?, U : ExternalModuleDependency?> addProviderConvertible(
        configurationName: String,
        dependencyNotation: ProviderConvertible<T>,
        configuration: Action<in U>,
    ) {
        TODO("Not yet implemented")
    }

    override fun <T : Any?> addProviderConvertible(
        configurationName: String,
        dependencyNotation: ProviderConvertible<T>,
    ) {
        TODO("Not yet implemented")
    }

    override fun create(dependencyNotation: Any): Dependency {
        TODO("Not yet implemented")
    }

    override fun create(dependencyNotation: Any, configureClosure: Closure<*>): Dependency {
        TODO("Not yet implemented")
    }

    override fun module(notation: Any): Dependency {
        TODO("Not yet implemented")
    }

    override fun module(notation: Any, configureClosure: Closure<*>): Dependency {
        TODO("Not yet implemented")
    }

    override fun project(notation: MutableMap<String, *>): Dependency {
        TODO("Not yet implemented")
    }

    override fun gradleApi(): Dependency {
        TODO("Not yet implemented")
    }

    override fun gradleTestKit(): Dependency {
        TODO("Not yet implemented")
    }

    override fun localGroovy(): Dependency {
        TODO("Not yet implemented")
    }

    override fun getConstraints(): DependencyConstraintHandler {
        TODO("Not yet implemented")
    }

    override fun constraints(configureAction: Action<in DependencyConstraintHandler>) {
        TODO("Not yet implemented")
    }

    override fun getComponents(): ComponentMetadataHandler {
        TODO("Not yet implemented")
    }

    override fun components(configureAction: Action<in ComponentMetadataHandler>) {
        TODO("Not yet implemented")
    }

    override fun getModules(): ComponentModuleMetadataHandler {
        TODO("Not yet implemented")
    }

    override fun modules(configureAction: Action<in ComponentModuleMetadataHandler>) {
        TODO("Not yet implemented")
    }

    override fun createArtifactResolutionQuery(): ArtifactResolutionQuery {
        TODO("Not yet implemented")
    }

    override fun attributesSchema(configureAction: Action<in AttributesSchema>): AttributesSchema {
        TODO("Not yet implemented")
    }

    override fun getAttributesSchema(): AttributesSchema {
        TODO("Not yet implemented")
    }

    override fun getArtifactTypes(): ArtifactTypeContainer {
        TODO("Not yet implemented")
    }

    override fun artifactTypes(configureAction: Action<in ArtifactTypeContainer>) {
        TODO("Not yet implemented")
    }

    override fun registerTransform(registrationAction: Action<in VariantTransform>) {
        TODO("Not yet implemented")
    }

    override fun <T : TransformParameters?> registerTransform(
        actionType: Class<out TransformAction<T>>,
        registrationAction: Action<in TransformSpec<T>>,
    ) {
        TODO("Not yet implemented")
    }

    override fun platform(notation: Any): Dependency {
        TODO("Not yet implemented")
    }

    override fun platform(notation: Any, configureAction: Action<in Dependency>): Dependency {
        TODO("Not yet implemented")
    }

    override fun enforcedPlatform(notation: Any): Dependency {
        TODO("Not yet implemented")
    }

    override fun enforcedPlatform(notation: Any, configureAction: Action<in Dependency>): Dependency {
        TODO("Not yet implemented")
    }

    override fun enforcedPlatform(dependencyProvider: Provider<MinimalExternalModuleDependency>): Provider<MinimalExternalModuleDependency> {
        TODO("Not yet implemented")
    }

    override fun testFixtures(notation: Any): Dependency {
        TODO("Not yet implemented")
    }

    override fun testFixtures(notation: Any, configureAction: Action<in Dependency>): Dependency {
        TODO("Not yet implemented")
    }

    override fun variantOf(
        dependencyProvider: Provider<MinimalExternalModuleDependency>,
        variantSpec: Action<in ExternalModuleDependencyVariantSpec>,
    ): Provider<MinimalExternalModuleDependency> {
        TODO("Not yet implemented")
    }
}
