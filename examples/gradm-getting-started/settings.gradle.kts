rootProject.name = "gradm-getting-started"

pluginManagement {
    includeBuild("gradm") // include Gradm here
}

plugins {
    id("com.gradle.develocity") version "3.17.6"
    id("gradm") // configured by GradmExtension
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
