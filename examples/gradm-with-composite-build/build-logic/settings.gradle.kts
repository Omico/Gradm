@file:Suppress("UnstableApiUsage")

rootProject.name = "gradm-with-composite-build-build-logic"

pluginManagement {
    includeBuild("gradm")
    repositories {
        google()
        mavenCentral()
        mavenLocal()
        maven(url = "https://s01.oss.sonatype.org/content/repositories/snapshots")
        gradlePluginPortal()
    }
}

buildscript {
    configurations.all {
        resolutionStrategy.cacheChangingModulesFor(0, TimeUnit.SECONDS)
    }
}

plugins {
    id("me.omico.gradm.generated")
}

include(":convention")
