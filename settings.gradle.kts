@file:Suppress("UnstableApiUsage")

rootProject.name = "Gradm"

pluginManagement {
    includeBuild("build-logic/initialization")
    includeBuild("build-logic/gradm")
    repositories {
        gradlePluginPortal {
            content {
                includeGroupByRegex("com.gradle.*")
            }
        }
        mavenCentral()
        maven(url = "https://s01.oss.sonatype.org/content/repositories/snapshots")
    }
}

plugins {
    id("initialization")
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

includeBuild("build-logic")

include(":gradm-codegen")
include(":gradm-gradle-plugin")
include(":gradm-integration")
include(":gradm-integration:api")
include(":gradm-integration:github")
include(":gradm-runtime")
include(":integration-testing")
