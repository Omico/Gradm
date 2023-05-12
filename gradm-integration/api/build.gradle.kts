plugins {
    kotlin("jvm")
    id("gradm.build-logic.maven-publish")
}

kotlin {
    jvmToolchain(11)
}

dependencies {
    api(projects.gradmRuntime)
}
