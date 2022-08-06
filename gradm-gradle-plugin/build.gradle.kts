import me.omico.age.dsl.withKotlinMavenPublication

plugins {
    `kotlin-dsl`
    `maven-publish`
}

withKotlinMavenPublication(mavenPublicationName = "gradlePlugin")

kotlin {
    target.compilations.all {
        kotlinOptions {
            jvmTarget = "11"
        }
    }
}

gradlePlugin {
    plugins {
        register("gradm") {
            id = "me.omico.gradm"
            implementationClass = "me.omico.gradm.GradmPlugin"
        }
    }
}

dependencies {
    implementation(projects.gradmCodegen)
    implementation(projects.gradmIntegration)
    implementation(projects.gradmRuntime)
}
