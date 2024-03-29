rootProject.name = "gradm-getting-started"

pluginManagement {
    includeBuild("gradm") // include Gradm here
}

plugins {
    id("com.gradle.enterprise") version "3.16.2"
    id("gradm") // configured by GradmExtension
}

gradleEnterprise {
    buildScan {
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"
        publishAlwaysIf(!gradle.startParameter.isOffline)
    }
}
