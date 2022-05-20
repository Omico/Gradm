import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import me.omico.age.spotless.androidXml
import me.omico.age.spotless.configureSpotless
import me.omico.age.spotless.intelliJIDEARunConfiguration

plugins {
    id("com.diffplug.spotless")
    id("com.github.ben-manes.versions")
    id("me.omico.age.project")
    id("me.omico.age.spotless")
}

val kotlinDslVersion = "2.1.7" // From embedded Kotlin

allprojects {
    group = "me.omico.gradm"
    version = "1.8.0-SNAPSHOT"
    configureDependencyUpdates(
        pinnedGroups = mapOf(
            "org.gradle.kotlin.kotlin-dsl" to kotlinDslVersion,
            "org.jetbrains.kotlin" to embeddedKotlinVersion,
            "org.jetbrains.kotlin.plugin.serialization" to embeddedKotlinVersion,
        ),
        pinnedModules = mapOf(
            "kotlinpoet" to versions.kotlinpoet,
            "kotlinx-coroutines-core" to versions.kotlinx.coroutines,
            "kotlinx-serialization-json" to versions.kotlinx.serialization,
            "org.gradle.kotlin.embedded-kotlin.gradle.plugin" to kotlinDslVersion,
        ),
    )
    configureSpotless {
        androidXml()
        intelliJIDEARunConfiguration()
        kotlin {
            target("src/**/*.kt")
            ktlint(versions.ktlint)
            indentWithSpaces()
            trimTrailingWhitespace()
            endWithNewline()
            licenseHeaderFile(rootProject.file("spotless/copyright.kt")).updateYearWithLatest(true).yearSeparator("-")
        }
        kotlinGradle {
            target("**/*.gradle.kts")
            targetExclude(".gradm/**/*.gradle.kts")
            ktlint(versions.ktlint)
            indentWithSpaces()
            trimTrailingWhitespace()
            endWithNewline()
        }
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

val wrapper: Wrapper by tasks.named("wrapper") {
    finalizedBy(updateGradleWrapper, updateGradleWrapperScripts)
}

val updateGradleWrapper by tasks.registering(Copy::class) {
    from(
        wrapper.jarFile,
        wrapper.propertiesFile,
    )
    into("example/gradle/wrapper")
}

val updateGradleWrapperScripts by tasks.registering(Copy::class) {
    from(
        wrapper.scriptFile,
        wrapper.batchScript,
    )
    into("example")
}
