plugins {
    `embedded-kotlin`
    id("gradm.build-logic.maven-publish")
}

kotlin {
    target.compilations.all {
        kotlinOptions {
            jvmTarget = "11"
        }
    }
}

dependencies {
    api(projects.gradmIntegration.api)
    api(projects.gradmIntegration.github)
}
