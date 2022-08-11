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
    compileOnly(gradleApi())
    compileOnly(projects.gradmRuntime)
    compileOnly(projects.gradmIntegration)
    implementation(libs.kotlinpoet)
}
