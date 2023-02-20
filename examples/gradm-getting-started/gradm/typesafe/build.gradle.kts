buildscript {
    configurations.all {
        resolutionStrategy.cacheChangingModulesFor(0, TimeUnit.SECONDS)
    }
}

plugins {
    `kotlin-dsl`
    id("me.omico.gradm.typesafe") version "3.1.0-SNAPSHOT"
}

gradmTypesafe {
    pluginId = "gradm.typesafe" // default: "me.omico.gradm.typesafe.generated"
}
