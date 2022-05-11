@file:Suppress("UnstableApiUsage")

rootProject.name = "gradm-example"

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        mavenLocal()
        maven(url = "https://s01.oss.sonatype.org/content/repositories/snapshots")
    }
    plugins {
        id("com.android.library") version "7.2.0"
        id("me.omico.gradm") version "1.7.0-SNAPSHOT"
        kotlin("android") version "1.6.21"
    }
}

plugins {
    id("me.omico.gradm")
}
