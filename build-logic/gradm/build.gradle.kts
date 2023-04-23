plugins {
    `kotlin-dsl`
    id("me.omico.gradm") version "3.2.1"
}

gradm {
    pluginId = "gradm"
    debug = true
    integrations {
        register("github")
    }
}
