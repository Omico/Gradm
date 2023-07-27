import me.omico.gradm.addDeclaredRepositories
import me.omico.gradm.initialization.includeGradm

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

addDeclaredRepositories()

plugins {
    id("gradm.gradm")
    id("gradm.gradle-enterprise")
}

includeBuild("build-logic/project")

includeGradm(":gradm-codegen")
includeGradm(":gradm-gradle-plugin")
includeGradm(":gradm-integration")
includeGradm(":gradm-integration:api")
includeGradm(":gradm-integration:github")
includeGradm(":gradm-runtime")

include(":integration-testing")
