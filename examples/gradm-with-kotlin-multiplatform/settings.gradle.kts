@file:Suppress("UnstableApiUsage")

rootProject.name = "gradm-with-kotlin-multiplatform"

pluginManagement {
    includeBuild("gradm")
}

plugins {
    id("com.gradle.develocity") version "3.17.5"
    id("gradm")
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
