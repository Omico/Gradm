plugins {
    id("gradm-with-composite-build.android.library")
}

dependencies {
    // Androidx
    compileOnly(androidx.activity.compose)
    compileOnly(androidx.compose.material)
    compileOnly(androidx.compose.material.icons.extended)
    compileOnly(androidx.compose.material3)
    compileOnly(androidx.compose.ui)
    compileOnly(androidx.compose.ui.tooling)
    compileOnly(androidx.compose.ui.tooling.preview)
    // Material
    compileOnly(material)
    // Square
    compileOnly(okhttp)
    compileOnly(okhttp.dnsOverHttps)
    compileOnly(okhttp.interceptor.logging)
}
