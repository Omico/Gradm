plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("gradm.build-logic.maven-publish")
}

kotlin {
    jvmToolchain(11)
}

dependencies {
    compileOnly(projects.gradmIntegration.api)
    implementation(libs.kotlinx.serialization.json)
}
