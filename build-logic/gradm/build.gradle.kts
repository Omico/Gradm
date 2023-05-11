plugins {
    `kotlin-dsl`
    id("me.omico.gradm") version "3.3.0"
}

repositories {
    mavenCentral()
}

gradm {
    pluginId = "gradm"
    debug = true
    integrations {
        register("github")
    }
}
