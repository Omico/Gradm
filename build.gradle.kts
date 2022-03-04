import me.omico.age.spotless.androidXml
import me.omico.age.spotless.configureSpotless
import me.omico.age.spotless.gradleVersionCatalogs
import me.omico.age.spotless.intelliJIDEARunConfiguration
import me.omico.age.spotless.kotlin
import me.omico.age.spotless.kotlinGradle

plugins {
    id("com.diffplug.spotless")
    id("com.github.ben-manes.versions")
    id("me.omico.age.project")
    id("me.omico.age.spotless")
}

allprojects {
    group = "me.omico.gradm"
    version = "1.0.0-SNAPSHOT"
    configureDependencyUpdates(
        stableGroups = listOf(
            "org.jetbrains.kotlin",
        ),
    )
    configureSpotless {
        androidXml()
        gradleVersionCatalogs()
        intelliJIDEARunConfiguration()
        kotlin(ktLintVersion = "0.43.2")
        kotlinGradle(ktLintVersion = "0.43.2")
    }
}

fun Project.configureDependencyUpdates(
    forcedArtifacts: List<String> = emptyList(),
    stableGroups: List<String> = emptyList(),
    stableModules: List<String> = emptyList(),
    stableArtifacts: List<String> = emptyList(),
    reservedGroups: Map<String, String> = emptyMap(),
    rejectedArtifacts: List<String> = emptyList(),
    rejectVersionIf: com.github.benmanes.gradle.versions.updates.resolutionstrategy.ComponentSelectionWithCurrent.() -> Boolean = { false },
) {
    configurations.all {
        resolutionStrategy.force(forcedArtifacts)
    }
    apply(plugin = "com.github.ben-manes.versions")
    tasks.withType<com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask> {
        rejectVersionIf {
            if (candidate.version == "") return@rejectVersionIf true
            if (rejectVersionIf(this)) return@rejectVersionIf true
            when {
                stableGroups.contains(candidate.group) -> isNonStable(candidate.version)
                stableModules.contains(candidate.module) -> isNonStable(candidate.version)
                stableArtifacts.contains("${candidate.group}:${candidate.module}") ->
                    isNonStable(candidate.version)
                reservedGroups.keys.contains(candidate.group) ->
                    candidate.version != reservedGroups[candidate.group]
                rejectedArtifacts.contains("${candidate.group}:${candidate.module}:${candidate.version}") -> true
                else -> false
            }
        }
    }
}

fun isNonStable(version: String): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.toUpperCase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(version)
    return isStable.not()
}
