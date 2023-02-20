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
        register("gradmTypesafe") {
            id = "me.omico.gradm.typesafe"
            implementationClass = "me.omico.gradm.typesafe.GradmTypesafePlugin"
        }
    }
}

dependencies {
    implementation(projects.gradmCodegen)
    implementation(projects.gradmRuntime)
}
