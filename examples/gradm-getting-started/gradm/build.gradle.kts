buildscript {
    configurations.all {
        resolutionStrategy.cacheChangingModulesFor(0, TimeUnit.SECONDS)
    }
}

plugins {
    `kotlin-dsl`
    id("me.omico.gradm") version "4.0.0-beta01"
    id("me.omico.gradm.integration.github") version "4.0.0-beta01"
}

repositories {
    mavenCentral()
}

gradm {
    pluginId = "gradm" // default: "me.omico.gradm.generated"
    configurationFilePath = "gradm3.yml" // default: "gradm.yml"
    debug = true // default: false
    format {
        enabled = true // default: true
        indent = 2 // default: 2
    }
    integrations {
        github {
            enabled = true // default: true
            configurationFilePath = "gradm.integration.github.yml" // default: "gradm.integration.github.yml"
        }
    }
    experimental {
        kotlinMultiplatformSupport = false // default: false
    }
}
