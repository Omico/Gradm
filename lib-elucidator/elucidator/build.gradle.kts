plugins {
    kotlin("jvm")
    id("gradm.build-logic.maven-publish")
}

kotlin {
    jvmToolchain(11)
    sourceSets.main {
        kotlin.srcDir("build/generated/kotlin")
    }
}

dependencies {
    compileOnly(libs.kotlinpoet)
}

dependencies {
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
}

tasks {
    compileKotlin {
        dependsOn(":elucidator-generator:run")
    }
    sourcesJar {
        dependsOn(":elucidator-generator:run")
    }
    test {
        useJUnitPlatform()
    }
    publish {
        dependsOn(":spotlessApply")
    }
    publishToMavenLocal {
        dependsOn(":spotlessApply")
    }
}
