@file:Suppress("UnstableApiUsage")

rootProject.name = "gradm-with-kotlin-multiplatform"

pluginManagement {
    includeBuild("gradm") // include Gradm here
    repositories {
        google()
        mavenCentral()
        mavenLocal()
        maven(url = "https://s01.oss.sonatype.org/content/repositories/snapshots")
        gradlePluginPortal()
    }
}

plugins {
    id("com.gradle.enterprise") version "3.13.3"
    id("gradm") // configured by GradmExtension
}

gradleEnterprise {
    buildScan {
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"
        publishAlwaysIf(!gradle.startParameter.isOffline)
    }
}
