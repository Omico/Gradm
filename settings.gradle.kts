@file:Suppress("UnstableApiUsage")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "Gradm"

pluginManagement {
    includeBuild("build-logic/gradm")
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

includeBuild("build-logic")

includeBuild("lib-elucidator") {
    dependencySubstitution {
        substitute(module("me.omico.elucidator:elucidator:0.1.0-SNAPSHOT")).using(project(":elucidator"))
    }
}

include(":gradm-codegen")
include(":gradm-gradle-plugin")
include(":gradm-integration")
include(":gradm-integration:api")
include(":gradm-integration:github")
include(":gradm-runtime")
include(":integration-testing")
