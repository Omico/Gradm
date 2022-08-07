plugins {
    `embedded-kotlin`
    kotlin("plugin.serialization")
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
    compileOnly(projects.gradmIntegration.api)
    implementation(libs.kotlinx.serialization)
}
