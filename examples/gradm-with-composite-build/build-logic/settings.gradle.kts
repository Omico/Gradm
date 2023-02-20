@file:Suppress("UnstableApiUsage")

rootProject.name = "build-logic"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    includeBuild("gradm")
    repositories {
        google()
        gradlePluginPortal {
            content {
                excludeGroupByRegex("me.omico.*") // reduce build time
            }
        }
        mavenCentral()
        mavenLocal()
        maven(url = "https://s01.oss.sonatype.org/content/repositories/snapshots")
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        gradlePluginPortal {
            content {
                excludeGroupByRegex("me.omico.*") // reduce build time
            }
        }
        mavenCentral()
        mavenLocal()
        maven(url = "https://s01.oss.sonatype.org/content/repositories/snapshots")
    }
}

plugins {
    id("me.omico.gradm.generated")
}

include(":convention")
include(":typesafe")
