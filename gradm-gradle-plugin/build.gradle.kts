import me.omico.age.dsl.withJavaSourcesJar
import me.omico.age.dsl.withJavadocJar

plugins {
    `kotlin-dsl`
    `maven-publish`
}

withJavaSourcesJar()
withJavadocJar()

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
