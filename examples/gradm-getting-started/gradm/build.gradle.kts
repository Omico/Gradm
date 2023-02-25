buildscript {
    configurations.all {
        resolutionStrategy.cacheChangingModulesFor(0, TimeUnit.SECONDS)
    }
}

plugins {
    `kotlin-dsl`
    id("me.omico.gradm") version "3.1.0-SNAPSHOT"
}

gradm {
    pluginId = "gradm" // default: "me.omico.gradm.generated"
    configFilePath = "gradm3.yml" // default: "gradm.yml"
    debug = true // default: false
    format {
        enabled = true // default: true
        indent = 2 // default: 2
    }
    integrations {
        apply("github") {
            enabled = true // default: true
            configFilePath = "gradm.integration.github.yml" // default: "gradm.integration.github.yml"
        }
    }
    experimental {
        kotlinMultiplatformSupport = false // default: false
    }
}
