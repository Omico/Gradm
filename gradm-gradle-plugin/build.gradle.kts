import me.omico.age.dsl.withKotlinMavenPublication

plugins {
    `kotlin-dsl`
}

withKotlinMavenPublication()

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
    compileOnly(gradleApi())
    compileOnly(gradleKotlinDsl())
    implementation(projects.gradmCodegen)
    implementation(projects.gradmIntegration)
    implementation(projects.gradmRuntime)
}
