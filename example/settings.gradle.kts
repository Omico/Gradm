@file:Suppress("UnstableApiUsage")

rootProject.name = "gradm-example"

pluginManagement {
    includeBuild("..")
    repositories {
        gradlePluginPortal()
        mavenCentral()
        mavenLocal()
        maven(url = "https://s01.oss.sonatype.org/content/repositories/snapshots")
    }
}

plugins {
    id("me.omico.gradm")
}
