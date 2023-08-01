import me.omico.gradm.addDeclaredRepositories
import me.omico.gradm.initialization.includeGradmApi

addDeclaredRepositories()

plugins {
    id("gradm.gradm")
}

includeBuild("../build-logic/project")

includeGradmApi(":datastore")
includeGradmApi(":test")
