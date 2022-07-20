@file:Suppress("UnstableApiUsage")

import me.omico.gradm.configs
import me.omico.gradm.gradm
import org.gradle.api.internal.FeaturePreviews

rootProject.name = "Gradm"

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        mavenLocal()
        maven(url = "https://s01.oss.sonatype.org/content/repositories/snapshots")
    }
}

plugins {
    id("com.gradle.enterprise") version "3.10.3"
    id("me.omico.gradm") version "2.2.0"
}

gradleEnterprise {
    buildScan {
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"
        publishAlwaysIf(!gradle.startParameter.isOffline)
    }
}

gradm {
    configs {
        format = true
    }
}

dependencyResolutionManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        mavenLocal()
        maven(url = "https://s01.oss.sonatype.org/content/repositories/snapshots")
    }
}

include(":gradm-codegen")
include(":gradm-gradle-plugin")
include(":gradm-integration")
include(":gradm-integration:api")
include(":gradm-integration:github")
include(":gradm-runtime")

FeaturePreviews.Feature.values()
    .filter { it.isActive }
    .forEach { enableFeaturePreview(it.name) }
