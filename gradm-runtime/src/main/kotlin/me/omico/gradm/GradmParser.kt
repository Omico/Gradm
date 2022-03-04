package me.omico.gradm

import me.omico.gradm.internal.asYamlDocument
import me.omico.gradm.internal.codegen.generateDependenciesProjectFiles
import me.omico.gradm.internal.config.dependencies
import me.omico.gradm.internal.config.gradmRuleVersion
import me.omico.gradm.internal.config.gradmVersion
import me.omico.gradm.internal.config.repositories
import me.omico.gradm.internal.maven.MavenRepositoryMetadataParser
import me.omico.gradm.internal.path.RootProjectPaths
import me.omico.gradm.internal.versionsMetaHash

object GradmParser {

    fun execute() {
        val document = RootProjectPaths.gradmConfig.toFile().inputStream().asYamlDocument()
        println("Gradm version: ${document.gradmVersion}")
        println("Gradm rule version: ${document.gradmRuleVersion}")
        MavenRepositoryMetadataParser.updateLastVersionsMetaHash()
        val dependencies = document.dependencies
        val versionsMeta = MavenRepositoryMetadataParser.updateVersionsMeta(dependencies, document.repositories)
        if (MavenRepositoryMetadataParser.lastVersionsMetaHash != versionsMetaHash || !isGradmGeneratedDependenciesSourcesExists) {
            generateDependenciesProjectFiles(document.gradmVersion, dependencies, versionsMeta)
        }
    }
}
