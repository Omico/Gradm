import me.omico.age.dsl.javaCompatibility
import me.omico.age.dsl.withKotlinMavenPublication

plugins {
    `embedded-kotlin`
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
    compileOnly(gradleApi())
    compileOnly(projects.gradmRuntime)
    @Suppress("GradlePackageUpdate") // Keep version compatible with built-in kotlin version.
    implementation("com.squareup:kotlinpoet:1.10.2")
}
