rootProject.name = "gradm-api"

pluginManagement {
    includeBuild("../build-logic/initialization")
}

plugins {
    id("gradm.api")
}
