@file:Suppress("UnstableApiUsage")

import me.omico.gradm.configs
import me.omico.gradm.gradm

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
        id("com.android.library") version "7.2.1"
        id("me.omico.gradm") version "2.1.0-SNAPSHOT"
        kotlin("android") version "1.7.0"
    }
}

plugins {
    id("me.omico.gradm")
}

gradm {
    configs {
        debug = true
        format = true
    }
}
