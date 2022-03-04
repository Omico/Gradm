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
    implementation("org.snakeyaml:snakeyaml-engine:2.3")
    implementation("com.squareup:kotlinpoet:1.10.2")
    @Suppress("GradlePackageUpdate") // Keep version compatible with built-in kotlin version.
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")
}
