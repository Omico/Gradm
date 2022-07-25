# Gradm with buildSrc

![Maven Central](https://img.shields.io/maven-central/v/me.omico.gradm/gradm-runtime)

In `buildSrc/settings.gradle.kts`, add the following:

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
    id("me.omico.gradm") version "2.3.0-SNAPSHOT"
}

gradm {
    configs {
        format = true
    }
}
```

Create a file named `gradm.yml` in your root project directory, like we see in [Getting Started](../gradm-getting-started).

## Notice

For updating tasks, due to we are using Gradm in buildSrc. So we need to use `-p, --project-dir` to specify the start directory for Gradle. For more information see [here](https://discuss.gradle.org/t/running-buildsrc-tasks-from-root-project/22069/2).

For example:

```shell
gradlew -p buildSrc gradmUpdateDependencies
```
