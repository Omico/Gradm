plugins {
    `embedded-kotlin`
}

kotlin {
    target.compilations.all {
        languageSettings.optIn("kotlin.RequiresOptIn")
        kotlinOptions {
            jvmTarget = "11"
        }
    }
}

dependencies {
    testImplementation(platform(junit.bom))
    testImplementation(junit.jupiter)
}

tasks.test {
    rootProject.allprojects
        .mapNotNull { project -> project.tasks.findByName("publishToMavenLocal") }
        .forEach { task -> dependsOn(task) }
    useJUnitPlatform()
}
