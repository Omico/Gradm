plugins {
    `kotlin-dsl`
    id("gradm.publishing")
}

kotlin {
    jvmToolchain(11)
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
