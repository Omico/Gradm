@file:Suppress("UnstableApiUsage")

rootProject.name = "build-logic"

pluginManagement {
    includeBuild("gradm")
}

plugins {
    id("gradm")
}

include(":convention")
