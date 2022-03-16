package me.omico.gradm

import me.omico.gradm.internal.asYamlDocument
import me.omico.gradm.internal.codegen.generateDependenciesProjectFiles
import me.omico.gradm.internal.config.gradmRuleVersion
import me.omico.gradm.internal.config.gradmVersion
import me.omico.gradm.internal.localVersionsMeta
import me.omico.gradm.internal.maven.MavenRepositoryMetadataParser
import me.omico.gradm.internal.path.RootProjectPaths

object GradmParser {

    fun execute(updateDependencies: Boolean = false) {
        val document = RootProjectPaths.gradmConfig.toFile().inputStream().asYamlDocument()
        println("Gradm version: ${document.gradmVersion}")
        println("Gradm rule version: ${document.gradmRuleVersion}")
        val versionsMeta = when {
            updateDependencies -> MavenRepositoryMetadataParser.updateVersionsMeta(document)
            else -> localVersionsMeta
        }
        generateDependenciesProjectFiles(document, versionsMeta)
    }
}
