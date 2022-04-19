@file:Suppress("UnstableApiUsage")

import org.gradle.api.internal.FeaturePreviews

rootProject.name = "Gradm"

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        mavenLocal()
        maven(url = "https://s01.oss.sonatype.org/content/repositories/snapshots")
    }
    val versions = object {
        val agePlugin = "1.0.0-SNAPSHOT"
        val gradleEnterprisePlugin = "3.8.1"
        val gradleVersionsPlugin = "0.42.0"
        val spotlessPlugin = "6.4.2"
    }
    plugins {
        id("com.diffplug.spotless") version versions.spotlessPlugin
        id("com.github.ben-manes.versions") version versions.gradleVersionsPlugin
        id("com.gradle.enterprise") version versions.gradleEnterprisePlugin
        id("me.omico.age.project") version versions.agePlugin
        id("me.omico.age.spotless") version versions.agePlugin
        kotlin("plugin.serialization") version embeddedKotlinVersion
    }
}

plugins {
    id("me.omico.gradm") version "1.5.0"
}

dependencyResolutionManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        mavenLocal()
        maven(url = "https://s01.oss.sonatype.org/content/repositories/snapshots")
    }
}

include(":gradm-codegen")
include(":gradm-integration")
include(":gradm-integration:api")
include(":gradm-integration:github")
include(":gradm-plugin")
include(":gradm-runtime")

FeaturePreviews.Feature.values()
    .filter { it.isActive }
    .forEach { enableFeaturePreview(it.name) }
