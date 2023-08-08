plugins {
    `kotlin-dsl`
    kotlin("plugin.serialization")
    id("gradm.publishing")
}

kotlin {
    jvmToolchain(11)
}

gradlePlugin {
    plugins {
        register("gradmIntegrationGithub") {
            id = "me.omico.gradm.integration.github"
            implementationClass = "me.omico.gradm.integration.github.GradmGitHubIntegrationPlugin"
        }
    }
}

dependencies {
    compileOnly(projects.gradmIntegration.gradmIntegrationApi)
    implementation(libs.kotlinx.serialization.json)
}
