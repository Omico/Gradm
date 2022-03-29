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
    api(projects.gradmIntegration.api)
    api(projects.gradmIntegration.github)
}
