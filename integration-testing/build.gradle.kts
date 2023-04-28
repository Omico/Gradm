plugins {
    `embedded-kotlin`
}

kotlin {
    jvmToolchain(11)
}

dependencies {
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
    useJUnitPlatform()
}
