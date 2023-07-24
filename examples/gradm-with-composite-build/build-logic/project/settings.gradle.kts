rootProject.name = "gradm-with-composite-build-project"

pluginManagement {
    includeBuild("../gradm")
}

plugins {
    id("gradm-with-composite-build.gradm")
}
