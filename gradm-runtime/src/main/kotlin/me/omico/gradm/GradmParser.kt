package me.omico.gradm

import me.omico.gradm.internal.asYamlDocument
import me.omico.gradm.internal.codegen.generateDependenciesProjectFiles
import me.omico.gradm.internal.config.gradmRuleVersion
import me.omico.gradm.internal.config.gradmVersion
import me.omico.gradm.internal.maven.MavenRepositoryMetadataParser
import me.omico.gradm.internal.path.RootProjectPaths

object GradmParser {

    fun execute() {
        val document = RootProjectPaths.gradmConfig.toFile().inputStream().asYamlDocument()
        println("Gradm version: ${document.gradmVersion}")
        println("Gradm rule version: ${document.gradmRuleVersion}")
        val versionsMeta = MavenRepositoryMetadataParser.updateVersionsMeta(document)
        generateDependenciesProjectFiles(document, versionsMeta)
    }
}
