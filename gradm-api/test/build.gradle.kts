plugins {
    kotlin("jvm")
}

kotlin {
    jvmToolchain(11)
}

dependencies {
    api(platform(libs.junit.bom))
    api(libs.junit.jupiter)
}
