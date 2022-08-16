import java.nio.file.Files
import kotlin.streams.toList

plugins {
    id("gradm.build-logic.root-project.base")
    id("gradm.build-logic.dependency-updates")
    id("gradm.build-logic.example-updater")
    id("gradm.build-logic.spotless")
}

val wrapper: Wrapper by tasks.named<Wrapper>("wrapper") {
    gradleVersion = versions.gradle
    distributionType = Wrapper.DistributionType.BIN
    finalizedBy(syncGradleWrapperForExamples)
}

val syncGradleWrapperForExamples by tasks.registering {
    Files.list(file("examples").toPath())
        .filter(Files::isDirectory)
        .filter { directory ->
            Files.list(directory)
                .filter { it.fileName.toString().endsWith(".gradle.kts") }
                .toList()
                .isNotEmpty()
        }
        .map { it.fileName }
        .forEach { example ->
            copy {
                from(wrapper.scriptFile, wrapper.batchScript)
                into("examples/$example")
            }
            copy {
                from(wrapper.jarFile, wrapper.propertiesFile)
                into("examples/$example/gradle/wrapper")
            }
        }
}
