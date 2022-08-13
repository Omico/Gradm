import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import me.omico.age.spotless.configureSpotless
import me.omico.age.spotless.intelliJIDEARunConfiguration
import me.omico.age.spotless.kotlin
import me.omico.age.spotless.kotlinGradle
import java.nio.file.Files

plugins {
    id("com.diffplug.spotless")
    id("com.github.ben-manes.versions")
    id("me.omico.age.project")
    id("me.omico.age.spotless")
}

allprojects {
    group = "me.omico.gradm"
    version = "2.4.0"
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
        kotlin(
            licenseHeaderFile = rootProject.file("spotless/copyright.kt"),
            licenseHeaderConfig = {
                updateYearWithLatest(true)
                yearSeparator("-")
            },
        )
        kotlinGradle(
            additionalExcludeTargets = setOf(
                ".gradm/**/*.gradle.kts",
            ),
        )
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
    finalizedBy(syncGradleWrapperForExamples)
}

val syncGradleWrapperForExamples by tasks.registering {
    file("examples").list()?.forEach {
        copy {
            from(wrapper.scriptFile, wrapper.batchScript)
            into("examples/$it")
        }
        copy {
            from(wrapper.jarFile, wrapper.propertiesFile)
            into("examples/$it/gradle/wrapper")
        }
    }
}

val syncGradmVersionForExamples by tasks.registering {
    Files.walk(file("examples").toPath())
        .filter { it.endsWith("settings.gradle.kts") }
        .map { it.toFile() }
        .forEach { file ->
            buildString {
                file.readLines().forEach {
                    val line = when {
                        it.startsWith("    id(\"me.omico.gradm\") version") ->
                            "    id(\"me.omico.gradm\") version \"${version}\""
                        else -> it
                    }
                    appendLine(line)
                }
            }.let { file.writeText(it) }
        }
}
