# Gradm with composite builds

![Maven Central](https://img.shields.io/maven-central/v/me.omico.gradm/gradm-runtime)

Create a folder named `build-logic` in your root directory.

In `build-logic/settings.gradle.kts`, add the following:

```kotlin
pluginManagement {
    repositories {
        // For the Kotlin DSL plugin.
        gradlePluginPortal()
        // For Gradm plugin.
        mavenCentral()
        // For Gradm plugin, if you are developing locally.
        mavenLocal()
        // For Gradm plugin, if you are using snapshot versions.
        maven(url = "https://s01.oss.sonatype.org/content/repositories/snapshots")
    }
}

dependencyResolutionManagement {
    // Declare repositories based on your own needs.
    repositories {
        // For Android Gradle plugins.
        google()
        // For common Gradle plugins.
        gradlePluginPortal()
        // For Gradm runtime or/and others.
        mavenCentral()
        // For Gradm runtime, if you are developing locally.
        mavenLocal()
        // For Gradm runtime, if you are using snapshot versions.
        maven(url = "https://s01.oss.sonatype.org/content/repositories/snapshots")
    }
}

plugins {
    id("me.omico.gradm") version "<replace-new-version-here>"
}

gradm {
    configs {
        format = true
    }
}
```

Create a file named `gradm.yml` in your `build-logic` directory, like we see in [Getting Started](../gradm-getting-started).

In `settings.gradle.kts`, add the following:

```kotlin
pluginManagement {
    includeBuild("build-logic")
}
```
