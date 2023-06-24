plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("gradm.publishing")
}

kotlin {
    jvmToolchain(11)
    sourceSets {
        all {
            languageSettings.optIn("kotlinx.serialization.ExperimentalSerializationApi")
        }
    }
}

dependencies {
    implementation(libs.kotlinx.serialization.protobuf)
}

dependencies {
    testImplementation(projects.gradmApiTest)
}

tasks {
    jar {
        dependsOn(test)
    }
}
