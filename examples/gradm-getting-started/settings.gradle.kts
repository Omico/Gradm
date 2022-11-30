@file:Suppress("UnstableApiUsage")

rootProject.name = "gradm-getting-started"

pluginManagement {
    repositories {
        gradlePluginPortal {
            content {
                excludeGroupByRegex("me.omico.*") // reduce build time
            }
        }
        google()
        mavenCentral()
        mavenLocal()
        maven(url = "https://s01.oss.sonatype.org/content/repositories/snapshots")
    }
}

plugins {
    id("me.omico.gradm") version "2.6.0"
}

gradm {
    configs {
        debug = true
        format = true
    }
}
