@file:Suppress("UnstableApiUsage")

import org.gradle.api.internal.FeaturePreviews

rootProject.name = "build-logic"

pluginManagement {
    repositories {
        google()
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

dependencyResolutionManagement {
    repositories {
        google()
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

buildCache {
    local {
        removeUnusedEntriesAfterDays = 1
    }
}

plugins {
    id("me.omico.gradm") version "2.4.0"
}

gradm {
    configs {
        format = true
    }
}

include(":convention")

FeaturePreviews.Feature.values()
    .filter { it.isActive }
    .forEach { enableFeaturePreview(it.name) }
