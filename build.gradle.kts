plugins {
    kotlin("jvm") apply false
    id("gradm.root")
}

listOf(
    "spotlessApply",
    "spotlessCheck",
).forEach { taskName ->
    tasks[taskName].dependsOn(gradle.includedBuild("gradm-api").task(":$taskName"))
}

listOf(
    "publishToMavenLocal",
    "publish",
).forEach { taskName ->
    tasks.register(taskName) {
        listOf(
            "datastore",
        ).forEach { module ->
            dependsOn(gradle.includedBuild("gradm-api").task(":gradm-api-$module:$taskName"))
        }
    }
}
