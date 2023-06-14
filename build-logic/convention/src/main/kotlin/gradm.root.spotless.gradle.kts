import me.omico.consensus.dsl.requireRootProject

plugins {
    id("me.omico.consensus.spotless")
}

requireRootProject()

consensus {
    spotless {
        rootProject {
            freshmark(
                excludeTargets = setOf("**/node_modules/**"),
            )
            gradleProperties()
            intelliJIDEARunConfiguration()
        }
        allprojects {
            kotlin(
                licenseHeaderFile = rootProject.file("spotless/copyright.kt"),
            )
            kotlinGradle()
        }
    }
}

subprojects {
    rootProject.tasks {
        spotlessApply { dependsOn(this@subprojects.tasks.spotlessApply) }
        spotlessCheck { dependsOn(this@subprojects.tasks.spotlessCheck) }
    }
}
