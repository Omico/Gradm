plugins {
    `kotlin-dsl`
    id("gradm.build-logic.maven-publish")
}

kotlin {
    target.compilations.all {
        kotlinOptions {
            jvmTarget = "11"
        }
    }
}

gradlePlugin {
    isAutomatedPublishing = false
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

dependencies {
    implementation(libs.gradle.kotlin.dsl.plugins)
}
