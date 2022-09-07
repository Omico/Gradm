@file:Suppress("UnstableApiUsage")

import org.gradle.api.internal.FeaturePreviews

rootProject.name = "Gradm"

pluginManagement {
    includeBuild("build-logic")
    repositories {
        gradlePluginPortal {
            content {
                excludeGroupByRegex("me.omico.*") // reduce build time
            }
        }
        mavenCentral()
        mavenLocal()
        maven(url = "https://s01.oss.sonatype.org/content/repositories/snapshots")
    }
}

buildscript {
    configurations.all {
        resolutionStrategy.cacheChangingModulesFor(0, TimeUnit.SECONDS)
    }
}

plugins {
    id("com.gradle.enterprise") version "3.11.1"
}

gradleEnterprise {
    buildScan {
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"
        publishAlwaysIf(!gradle.startParameter.isOffline)
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

buildCache {
    local {
        removeUnusedEntriesAfterDays = 1
    }
}

include(":gradm-codegen")
include(":gradm-gradle-plugin")
include(":gradm-integration")
include(":gradm-integration:api")
include(":gradm-integration:github")
include(":gradm-runtime")
include(":integration-testing")

FeaturePreviews.Feature.values()
    .filter { it.isActive }
    .forEach { enableFeaturePreview(it.name) }
