import me.omico.age.dsl.withKotlinMavenPublication

plugins {
    `embedded-kotlin`
}

withKotlinMavenPublication()

kotlin {
    target.compilations.all {
        kotlinOptions {
            jvmTarget = "11"
            freeCompilerArgs = listOf(
                "-opt-in=kotlin.RequiresOptIn",
            )
        }
    }
}

dependencies {
    compileOnly(gradleApi())
    compileOnly(projects.gradmRuntime)
    compileOnly(projects.gradmIntegration)
    implementation(libs.kotlinpoet)
}
