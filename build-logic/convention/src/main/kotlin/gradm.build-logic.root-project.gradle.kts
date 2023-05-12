import java.nio.file.Path
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.walk

plugins {
    id("gradm")
    id("gradm.build-logic.root-project.base")
    id("gradm.build-logic.example-updater")
    id("gradm.build-logic.git.hooks")
    id("gradm.build-logic.gradle-enterprise-updater")
    id("gradm.build-logic.spotless")
}

val wrapper: Wrapper by tasks.named<Wrapper>("wrapper") {
    gradleVersion = versions.gradle
    distributionType = Wrapper.DistributionType.BIN
    finalizedBy(syncGradleWrapperForExamples)
}

@OptIn(ExperimentalPathApi::class)
val syncGradleWrapperForExamples by tasks.registering {
    file("examples").toPath().listDirectoryEntries()
        .filter { directory ->
            directory.walk()
                .filter { it.fileName.toString().endsWith(".gradle.kts") }
                .toList()
                .isNotEmpty()
        }
        .map(Path::getFileName)
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
