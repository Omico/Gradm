@file:Suppress("UnstableApiUsage")

plugins {
    kotlin("android")
    id("com.android.library")
}

android {
    compileSdk = 33
    namespace = "me.omico.gradm.example"
    defaultConfig {
        minSdk = 21
    }
    composeOptions {
        // You must add gradmGeneratedJar to dependencies, otherwise you won't be able to use.
        kotlinCompilerExtensionVersion = versions.androidx.compose.compiler
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

kotlin {
    jvmToolchain(11)
}
