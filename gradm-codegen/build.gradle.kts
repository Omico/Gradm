plugins {
    kotlin("jvm")
    id("gradm.publishing")
}

kotlin {
    jvmToolchain(11)
}

dependencies {
    compileOnly(gradleApi())
    compileOnly(projects.gradmRuntime)
    compileOnly(projects.gradmIntegration.gradmIntegrationRuntime)
    implementation(libs.elucidator)
    implementation(libs.kotlinpoet)
}
