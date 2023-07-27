plugins {
    `kotlin-dsl`
    id("me.omico.gradm") version "3.3.3"
}

repositories {
    mavenCentral()
}

gradm {
    pluginId = "gradm.gradm"
    debug = true
    integrations {
        register("github")
    }
}
