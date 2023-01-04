plugins {
    `kotlin-dsl`
    id("me.omico.gradm") version "3.0.0"
}

gradm {
    pluginId = "gradm"
    debug = true
    integrations {
        apply("github")
    }
}
