plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("gradm.publishing")
}

kotlin {
    jvmToolchain(11)
}

dependencies {
    compileOnly(projects.gradmIntegration.gradmIntegrationApi)
    implementation(libs.kotlinx.serialization.json)
}
