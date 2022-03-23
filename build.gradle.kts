import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import me.omico.age.spotless.androidXml
import me.omico.age.spotless.configureSpotless
import me.omico.age.spotless.intelliJIDEARunConfiguration
import me.omico.age.spotless.kotlinGradle

plugins {
    id("com.diffplug.spotless")
    id("com.github.ben-manes.versions")
    id("me.omico.age.project")
    id("me.omico.age.spotless")
}

val ktLintVersion = "0.43.2"
val kotlinDslVersion = "2.1.7"

allprojects {
    group = "me.omico.gradm"
    version = "1.2.0-SNAPSHOT"
    configureDependencyUpdates(
        pinnedGroups = mapOf(
            "org.jetbrains.kotlin" to embeddedKotlinVersion,
            "org.gradle.kotlin.kotlin-dsl" to kotlinDslVersion,
        ),
        pinnedModules = mapOf(
            "org.gradle.kotlin.embedded-kotlin.gradle.plugin" to kotlinDslVersion,
            "kotlinx-coroutines-core" to "1.5.2", // https://github.com/Kotlin/kotlinx.coroutines
        ),
    )
    configureSpotless {
        androidXml()
        intelliJIDEARunConfiguration()
        kotlin {
            target("src/**/*.kt")
            ktlint(ktLintVersion)
            indentWithSpaces()
            trimTrailingWhitespace()
            endWithNewline()
            licenseHeaderFile(rootProject.file("spotless/copyright.kt")).updateYearWithLatest(true).yearSeparator("-")
        }
        kotlinGradle(ktLintVersion = ktLintVersion)
    }
}

fun Project.configureDependencyUpdates(
    pinnedGroups: Map<String, String> = emptyMap(),
    pinnedModules: Map<String, String> = emptyMap(),
) {
    apply(plugin = "com.github.ben-manes.versions")
    tasks.withType<DependencyUpdatesTask> {
        rejectVersionIf {
            if (candidate.version == "") return@rejectVersionIf true
            when {
                pinnedGroups.keys.contains(candidate.group) -> candidate.version != pinnedGroups[candidate.group]
                pinnedModules.keys.contains(candidate.module) -> candidate.version != pinnedModules[candidate.module]
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
