import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
    application
}

group = "me.omico.gradm.example"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
    google()
}

dependencies {
    compileOnly(androidx.compose.ui)
    compileOnly(androidx.compose.animation)
    compileOnly(androidx.appcompat)
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

application {
    mainClass.set("MainKt")
}
