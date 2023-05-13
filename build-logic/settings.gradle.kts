@file:Suppress("UnstableApiUsage")

rootProject.name = "build-logic"

pluginManagement {
    includeBuild("gradm")
    repositories {
        mavenCentral()
        maven(url = "https://s01.oss.sonatype.org/content/repositories/snapshots")
        gradlePluginPortal()
    }
}

plugins {
    id("gradm")
}

include(":convention")
