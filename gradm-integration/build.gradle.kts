plugins {
    `embedded-kotlin`
    id("gradm.build-logic.maven-publish")
}

kotlin {
    jvmToolchain(11)
}

dependencies {
    api(projects.gradmIntegration.api)
    api(projects.gradmIntegration.github)
}
