import me.omico.gradm.dependency.Okhttp

plugins {
    kotlin("multiplatform")
}

kotlin {
    jvm()
    sourceSets {
        commonMain {
            dependencies {
                // Assume okhttp conflict with other plugins,
                // you can directly use me.omico.gradm.dependency.Okhttp instead.
                implementation(Okhttp)
            }
        }
    }
}
