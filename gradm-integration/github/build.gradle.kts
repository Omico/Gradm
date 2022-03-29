import me.omico.age.dsl.javaCompatibility
import me.omico.age.dsl.withKotlinMavenPublication

plugins {
    `embedded-kotlin`
    kotlin("plugin.serialization")
}

javaCompatibility(all = JavaVersion.VERSION_11)
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
    @Suppress("GradlePackageUpdate") // Keep version compatible with built-in kotlin version.
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.1")
}
