plugins {
    `kotlin-dsl`
    id("me.omico.gradm") version "3.3.1"
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
