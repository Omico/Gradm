plugins {
    kotlin("jvm")
    id("gradm.build-logic.maven-publish")
}

kotlin {
    jvmToolchain(11)
    explicitApi()
    sourceSets.main {
        kotlin.srcDir("build/generated/kotlin")
    }
}

dependencies {
    compileOnly(libs.kotlinpoet)
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation(libs.kotlinpoet)
}

tasks {
    listOf(compileKotlin, sourcesJar).forEach { task ->
        task {
            dependsOn(":elucidator-generator:run")
        }
    }
    listOf(publish, publishToMavenLocal).forEach { task ->
        task {
            dependsOn(":spotlessApply", ":elucidator:test")
        }
    }
}
