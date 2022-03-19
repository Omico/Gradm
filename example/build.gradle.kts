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
    compileOnly(androidx.activity.activityKtx)
    compileOnly(androidx.activity.activityCompose)
    compileOnly(androidx.annotation.annotation)
    compileOnly(androidx.appcompat.appcompat)
    compileOnly(androidx.camera.camera2)
    compileOnly(androidx.camera.lifecycle)
    compileOnly(androidx.camera.view)
    compileOnly(androidx.compose.animation)
    compileOnly(androidx.compose.foundation)
    compileOnly(androidx.compose.material)
    compileOnly(androidx.compose.materialIconsCore)
    compileOnly(androidx.compose.materialIconsExtended)
    compileOnly(androidx.compose.runtime)
    compileOnly(androidx.compose.ui)
    compileOnly(androidx.compose.uiTooling)
    compileOnly(androidx.compose.uiToolingPreview)
    compileOnly(androidx.compose.uiUtil)
    compileOnly(androidx.core.coreKtx)
    compileOnly(androidx.datastore.datastore)
    compileOnly(androidx.lifecycle.runtimeKtx)
    compileOnly(androidx.navigation.compose)
    compileOnly(androidx.navigation.runtimeKtx)
    // Square
    compileOnly(square.okhttp3.dnsOverHttps)
    compileOnly(square.okhttp3.interceptor.logging)
    compileOnly(square.okhttp3.okhttp)
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}
