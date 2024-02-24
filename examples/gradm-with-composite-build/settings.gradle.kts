rootProject.name = "gradm-with-composite-build-root"

pluginManagement {
    repositories {
        maven(url = "https://maven.omico.me")
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    includeBuild("build-logic/gradm")
}

buildscript {
    configurations.all {
        resolutionStrategy.cacheChangingModulesFor(0, TimeUnit.SECONDS)
    }
}

plugins {
    id("com.gradle.enterprise") version "3.16.2"
    id("gradm-with-composite-build.gradm")
}

gradleEnterprise {
    buildScan {
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"
        publishAlwaysIf(!gradle.startParameter.isOffline)
    }
}

includeBuild("build-logic/project")

include(":example")
