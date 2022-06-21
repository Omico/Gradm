import me.omico.age.dsl.withKotlinMavenPublication

plugins {
    `embedded-kotlin`
    kotlin("plugin.serialization")
}

withKotlinMavenPublication()

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
