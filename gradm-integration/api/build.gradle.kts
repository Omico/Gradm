import me.omico.age.dsl.withKotlinMavenPublication

plugins {
    `embedded-kotlin`
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
    api(gradleApi())
    api(projects.gradmRuntime)
}
