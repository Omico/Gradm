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
    api(gradleApi())
    api(projects.gradmRuntime)
}
