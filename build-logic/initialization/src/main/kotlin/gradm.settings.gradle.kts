import me.omico.gradm.addDeclaredRepositories
import me.omico.gradm.initialization.gradmApiModules
import me.omico.gradm.initialization.includeGradm
import me.omico.gradm.initialization.syncPropertiesToGradmApi

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

addDeclaredRepositories()

plugins {
    id("gradm.gradm")
    id("gradm.gradle-enterprise")
}

includeBuild("build-logic/project")

syncPropertiesToGradmApi()
includeBuild("gradm-api") {
    dependencySubstitution {
        val version = extra["PROJECT_VERSION"] as String
        gradmApiModules.forEach { module ->
            substitute(module("me.omico.gradm:gradm-api-$module:$version")).using(project(":gradm-api-$module"))
        }
    }
}

includeGradm(":gradm-codegen")
includeGradm(":gradm-gradle-plugin")
includeGradm(":gradm-integration")
includeGradm(":gradm-integration:api")
includeGradm(":gradm-integration:github")
includeGradm(":gradm-runtime")

include(":integration-testing")
