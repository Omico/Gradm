@file:Suppress("UnstableApiUsage")

rootProject.name = rootProject.projectDir.name

pluginManagement {
    includeBuild("../build-logic/gradm")
    repositories {
        mavenCentral()
        maven(url = "https://s01.oss.sonatype.org/content/repositories/snapshots")
        gradlePluginPortal()
    }
}

plugins {
    id("gradm")
    id("com.gradle.enterprise") version "3.13.2"
}

gradleEnterprise {
    buildScan {
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"
        publishAlwaysIf(!gradle.startParameter.isOffline)
    }
}

includeBuild("../build-logic")

include(":elucidator")
include(":elucidator-generator")
