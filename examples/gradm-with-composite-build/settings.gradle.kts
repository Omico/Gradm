@file:Suppress("UnstableApiUsage")

rootProject.name = "gradm-with-composite-build"

pluginManagement {
    includeBuild("build-logic/gradm")
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
    id("com.gradle.enterprise") version "3.13.4"
    id("me.omico.gradm.generated")
}

gradleEnterprise {
    buildScan {
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"
        publishAlwaysIf(!gradle.startParameter.isOffline)
    }
}

includeBuild("build-logic")

include(":example")
