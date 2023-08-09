plugins {
    `kotlin-dsl`
    id("me.omico.gradm") version "4.0.0-beta02"
    id("me.omico.gradm.integration.github") version "4.0.0-beta02"
}

repositories {
    mavenCentral()
}

gradm {
    pluginId = "gradm.gradm"
    debug = true
}
