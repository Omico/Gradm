import me.omico.gradm.dependency.Androidx

plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

kotlin {
    android()
    sourceSets {
        named("androidMain") {
            dependencies {
                implementation(accompanist.insets)
                // Assume androidx conflict with other plugins,
                // you can directly use me.omico.gradm.dependency.Androidx instead.
                implementation(Androidx.activity.ktx)
            }
        }
    }
}

android {
    compileSdk = 33
    namespace = "me.omico.gradm.example"
    defaultConfig {
        minSdk = 21
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}
