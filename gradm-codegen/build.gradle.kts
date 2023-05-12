plugins {
    kotlin("jvm")
    id("gradm.build-logic.maven-publish")
}

kotlin {
    jvmToolchain(11)
}

dependencies {
    compileOnly(gradleApi())
    compileOnly(projects.gradmRuntime)
    compileOnly(projects.gradmIntegration)
    implementation(libs.kotlinpoet)
}
