buildscript {
    configurations.all {
        resolutionStrategy.cacheChangingModulesFor(0, TimeUnit.SECONDS)
    }
}

plugins {
    `kotlin-dsl`
    id("me.omico.gradm") version "3.4.0-SNAPSHOT"
}

repositories {
    mavenCentral()
}

gradm {
    pluginId = "gradm-with-composite-build.gradm"
    debug = true
}
