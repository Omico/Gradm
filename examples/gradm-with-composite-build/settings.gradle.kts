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
    id("com.gradle.develocity") version "3.17.5"
    id("gradm-with-composite-build.gradm")
}

develocity {
    buildScan {
        termsOfUseUrl = "https://gradle.com/terms-of-service"
        termsOfUseAgree = "yes"
        publishing {
            val isOffline = providers.provider { gradle.startParameter.isOffline }
            onlyIf { !isOffline.getOrElse(false) }
        }
    }
}

includeBuild("build-logic/project")

include(":example")
