import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.library") version "7.0.4"
    kotlin("android") version "1.6.10"
}

repositories {
    mavenCentral()
    google()
}

android {
    compileSdk = 32
    defaultConfig {
        minSdk = 26
    }
}

dependencies {
    // Accompanist
    compileOnly(accompanist.insets)
    compileOnly(accompanist.insetsUi)
    compileOnly(accompanist.permissions)
    compileOnly(accompanist.swipeRefresh)
    compileOnly(accompanist.systemUiController)
    // Androidx
    compileOnly(androidx.activity.compose)
    compileOnly(androidx.activity.ktx)
    compileOnly(androidx.annotation)
    compileOnly(androidx.appcompat)
    compileOnly(androidx.camera.camera2)
    compileOnly(androidx.camera.lifecycle)
    compileOnly(androidx.camera.view)
    compileOnly(androidx.compose.animation)
    compileOnly(androidx.compose.foundation)
    compileOnly(androidx.compose.material)
    compileOnly(androidx.compose.material.icons.core)
    compileOnly(androidx.compose.material.icons.extended)
    compileOnly(androidx.compose.runtime)
    compileOnly(androidx.compose.ui)
    compileOnly(androidx.compose.ui.tooling)
    compileOnly(androidx.compose.ui.tooling.preview)
    compileOnly(androidx.compose.ui.util)
    compileOnly(androidx.core.ktx)
    compileOnly(androidx.datastore)
    compileOnly(androidx.lifecycle.runtime.ktx)
    compileOnly(androidx.navigation.compose)
    compileOnly(androidx.navigation.runtime.ktx)
    // Square
    compileOnly(square.okhttp3)
    compileOnly(square.okhttp3.dnsOverHttps)
    compileOnly(square.okhttp3.interceptor.logging)
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}
