plugins {
    kotlin("jvm")
    id("gradm.publishing")
}

kotlin {
    jvmToolchain(11)
}

dependencies {
    api(projects.gradmIntegration.gradmIntegrationApi)
}
