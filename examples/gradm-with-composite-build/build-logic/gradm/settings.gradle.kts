@file:Suppress("UnstableApiUsage")

rootProject.name = "gradm-with-composite-build-gradm"

pluginManagement {
    repositories {
        mavenCentral()
        mavenLocal()
        maven(url = "https://s01.oss.sonatype.org/content/repositories/snapshots")
        gradlePluginPortal()
    }
}
