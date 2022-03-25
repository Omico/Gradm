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
        id("me.omico.gradm") version "1.2.0-SNAPSHOT"
    }
}

plugins {
    id("me.omico.gradm")
}
