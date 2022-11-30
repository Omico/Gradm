@file:Suppress("UnstableApiUsage")

rootProject.name = "gradm"

pluginManagement {
    repositories {
        gradlePluginPortal {
            content {
                excludeGroupByRegex("me.omico.*") // reduce build time
            }
        }
        mavenLocal()
        maven(url = "https://s01.oss.sonatype.org/content/repositories/snapshots")
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        mavenLocal()
        maven(url = "https://s01.oss.sonatype.org/content/repositories/snapshots")
    }
}
