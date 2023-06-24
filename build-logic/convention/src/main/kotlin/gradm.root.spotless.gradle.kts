import me.omico.consensus.dsl.requireRootProject
import me.omico.consensus.dsl.rootGradle

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
                licenseHeaderFile = rootGradle.rootProject.file("spotless/copyright.kt"),
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
