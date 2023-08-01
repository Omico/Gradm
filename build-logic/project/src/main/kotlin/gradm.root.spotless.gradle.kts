import me.omico.consensus.dsl.requireRootProject
import me.omico.consensus.dsl.rootGradle
import me.omico.consensus.spotless.ConsensusSpotlessTokens

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
                targets = ConsensusSpotlessTokens.Kotlin.targets + setOf(
                    "build-logic/initialization/src/**/*.kt",
                ),
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
