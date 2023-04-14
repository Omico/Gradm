plugins {
    `embedded-kotlin`
    id("gradm.build-logic.maven-publish")
}

kotlin {
    jvmToolchain(11)
}

dependencies {
    api(gradleApi())
    api(projects.gradmRuntime)
}
