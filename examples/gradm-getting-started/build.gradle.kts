plugins {
    kotlin("jvm")
}

dependencies {
    // DSL is generated by Gradm
    compileOnly(okhttp)
    compileOnly(okhttp.dnsOverHttps)
    compileOnly(okhttp.interceptor.logging)
}
