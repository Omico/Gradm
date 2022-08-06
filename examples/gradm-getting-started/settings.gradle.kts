@file:Suppress("UnstableApiUsage")

rootProject.name = "gradm-getting-started"

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        mavenLocal()
        maven(url = "https://s01.oss.sonatype.org/content/repositories/snapshots")
    }
}

plugins {
    id("me.omico.gradm") version "2.4.0-SNAPSHOT"
}

gradm {
    configs {
        debug = true
        format = true
    }
}
