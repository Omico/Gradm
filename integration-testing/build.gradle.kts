plugins {
    kotlin("jvm")
}

kotlin {
    jvmToolchain(11)
}

dependencies {
    testImplementation(gradleTestKit())
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
}

tasks.test {
    val requiredTasks = listOf("test", "publishToMavenLocal")
    rootProject.allprojects {
        requiredTasks.forEach {
            val task = tasks.findByName(it) ?: return@forEach
            if (task == this@test) return@forEach
            this@test.dependsOn(task)
        }
    }
    inputs.dir("../examples")
    useJUnitPlatform()
}
