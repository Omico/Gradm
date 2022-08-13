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
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
}

tasks.test {
    rootProject.allprojects
        .mapNotNull { project -> project.tasks.findByName("publishToMavenLocal") }
        .forEach { task -> dependsOn(task) }
    useJUnitPlatform()
}
