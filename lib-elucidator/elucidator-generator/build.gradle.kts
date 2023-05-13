plugins {
    application
    kotlin("jvm")
}

application {
    applicationName = "DslGenerator"
    mainClass.set("me.omico.elucidator.DslGenerator")
}

kotlin {
    jvmToolchain(11)
}

dependencies {
    implementation(libs.kotlinpoet)
}

val cleanup by tasks.registering {
    doLast {
        delete(projectDir.resolveSibling("elucidator/build/generated/kotlin"))
    }
}

tasks.run<JavaExec> {
    dependsOn(cleanup)
    args = listOf(projectDir.resolveSibling("elucidator/build/generated/kotlin").absolutePath)
}
