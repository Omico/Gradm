import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import me.omico.age.spotless.configureSpotless
import me.omico.age.spotless.defaultEditorConfig
import me.omico.age.spotless.intelliJIDEARunConfiguration

plugins {
    id("com.diffplug.spotless")
    id("com.github.ben-manes.versions")
    id("me.omico.age.project")
    id("me.omico.age.spotless")
}

allprojects {
    group = "me.omico.gradm"
    version = "2.3.0-SNAPSHOT"
    configureDependencyUpdates(
        pinnedGroups = mapOf(
            "org.gradle.kotlin.kotlin-dsl" to versions.kotlin.dsl,
            "org.jetbrains.kotlin" to versions.kotlin.toString(),
            "org.jetbrains.kotlin.plugin.serialization" to versions.kotlin.toString(),
        ),
        pinnedModules = mapOf(
            "kotlinpoet" to versions.kotlinpoet,
            "kotlinx-coroutines-core" to versions.kotlinx.coroutines,
            "kotlinx-serialization-json" to versions.kotlinx.serialization,
            "org.gradle.kotlin.embedded-kotlin.gradle.plugin" to versions.kotlin.dsl,
        ),
    )
    configureSpotless {
        intelliJIDEARunConfiguration()
        kotlin {
            target("src/**/*.kt")
            ktlint()
                .editorConfigOverride(defaultEditorConfig)
            indentWithSpaces()
            trimTrailingWhitespace()
            endWithNewline()
            licenseHeaderFile(rootProject.file("spotless/copyright.kt")).updateYearWithLatest(true).yearSeparator("-")
        }
        kotlinGradle {
            target("**/*.gradle.kts")
            targetExclude(".gradm/**/*.gradle.kts")
            ktlint()
                .editorConfigOverride(defaultEditorConfig)
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

val wrapper: Wrapper by tasks.named<Wrapper>("wrapper") {
    gradleVersion = versions.gradle
    distributionType = Wrapper.DistributionType.BIN
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
