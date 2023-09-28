@file:Suppress("UnstableApiUsage")

rootProject.name = "gradm-with-kotlin-multiplatform"

pluginManagement {
    includeBuild("gradm")
}

plugins {
    id("com.gradle.enterprise") version "3.15"
    id("gradm")
}

gradleEnterprise {
    buildScan {
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"
        publishAlwaysIf(!gradle.startParameter.isOffline)
    }
}
