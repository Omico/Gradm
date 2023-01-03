@file:Suppress("UnstableApiUsage")

plugins {
    id("com.android.library")
}

android {
    compileSdk = 32
    namespace = "me.omico.gradm.example"
    defaultConfig {
        minSdk = 21
        targetSdk = 32
    }
    composeOptions {
        // You must add gradmGeneratedJar to dependencies, otherwise you won't be able to use.
        kotlinCompilerExtensionVersion = versions.androidx.compose.compiler
    }
}
