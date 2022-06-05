@file:Suppress("UnstableApiUsage")

import me.omico.gradm.configs
import me.omico.gradm.gradm
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
        val gradleEnterprisePlugin = "3.10"
        val gradleVersionsPlugin = "0.42.0"
        val gradmPlugin = "2.0.0"
        val spotlessPlugin = "6.6.1"
    }
    plugins {
        id("com.diffplug.spotless") version versions.spotlessPlugin
        id("com.github.ben-manes.versions") version versions.gradleVersionsPlugin
        id("com.gradle.enterprise") version versions.gradleEnterprisePlugin
        id("me.omico.age.project") version versions.agePlugin
        id("me.omico.age.spotless") version versions.agePlugin
        id("me.omico.gradm") version versions.gradmPlugin
        kotlin("plugin.serialization") version embeddedKotlinVersion
    }
}

plugins {
    id("com.gradle.enterprise")
    id("me.omico.gradm")
}

gradleEnterprise {
    buildScan {
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"
        publishAlwaysIf(!gradle.startParameter.isOffline)
    }
}

gradm {
    configs {
        format = true
    }
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
include(":gradm-gradle-plugin")
include(":gradm-integration")
include(":gradm-integration:api")
include(":gradm-integration:github")
include(":gradm-runtime")

FeaturePreviews.Feature.values()
    .filter { it.isActive }
    .forEach { enableFeaturePreview(it.name) }
