import me.omico.consensus.dsl.requireRootProject

plugins {
    id("gradm.gradm")
    id("gradm.root.examples-updater")
    id("gradm.root.git")
    id("gradm.root.spotless")
}

requireRootProject()

consensus {
    allprojects {
        group = gradleProperty("project.group")
        version = gradleProperty("project.version")
    }
}

afterEvaluate {
    require(embeddedKotlinVersion == versions.kotlin) {
        "Embedded Kotlin version must be the same as the Kotlin version used by Gradm"
    }
}

val wrapper: Wrapper by tasks.named<Wrapper>("wrapper") {
    gradleVersion = versions.gradle
    distributionType = Wrapper.DistributionType.BIN
    finalizedBy(syncGradleWrapperForExamples)
}

val syncGradleWrapperForExamples by tasks.registering {
    file("examples").walk().maxDepth(1)
        .filter { directory -> directory.resolve("settings.gradle.kts").exists() }
        .map(File::getName)
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
