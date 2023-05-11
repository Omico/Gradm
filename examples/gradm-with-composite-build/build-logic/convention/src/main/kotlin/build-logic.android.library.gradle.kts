@file:Suppress("UnstableApiUsage")

import me.omico.gradm.Versions.androidx
import me.omico.gradm.dependency.Androidx

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
        kotlinCompilerExtensionVersion = androidx.compose.compiler
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    compileOnly(Androidx.annotation)
}

kotlin {
    jvmToolchain(11)
}
