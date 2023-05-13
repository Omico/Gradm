plugins {
    kotlin("jvm") apply false
    id("gradm.build-logic.root-project")
}

tasks {
    prepareKotlinBuildScriptModel {
        dependsOn(gradle.includedBuild("lib-elucidator").task(":elucidator-generator:run"))
    }
    spotlessApply {
        dependsOn(gradle.includedBuild("lib-elucidator").task(":spotlessApply"))
    }
}
