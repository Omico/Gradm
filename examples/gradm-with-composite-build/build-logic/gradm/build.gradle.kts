buildscript {
    configurations.all {
        resolutionStrategy.cacheChangingModulesFor(0, TimeUnit.SECONDS)
    }
}

plugins {
    `kotlin-dsl`
    id("me.omico.gradm") version "3.3.0"
}

repositories {
    mavenCentral()
}

gradm {
    debug = true
}
