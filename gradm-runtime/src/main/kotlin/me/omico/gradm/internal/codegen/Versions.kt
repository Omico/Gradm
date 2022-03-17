package me.omico.gradm.internal.codegen

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import me.omico.gradm.internal.YamlDocument
import me.omico.gradm.internal.config.versions
import me.omico.gradm.internal.path.GradmPaths

internal data class CodegenVersions(
    val name: String,
    var version: String?,
    val subVersions: HashMap<String, CodegenVersions>,
)

internal fun generateVersionsSourceFile(document: YamlDocument) {
    createCodegenVersions(document).toFileSpec().writeTo(GradmPaths.GeneratedDependenciesProject.sourceDir)
}

private fun createCodegenVersions(document: YamlDocument): CodegenVersions =
    CodegenVersions(
        name = "Versions",
        version = null,
        subVersions = HashMap<String, CodegenVersions>()
            .apply {
                document.versions.forEach {
                    addVersion(it.key.removePrefix("versions."), it.value)
                }
            }
    )

private fun HashMap<String, CodegenVersions>.addVersion(key: String, version: String): Unit =
    when {
        key.contains(".") -> {
            val strings = key.split(".")
            val name = strings.first()
            val subKey = strings.drop(1).joinToString(".")
            getOrCreate(name).subVersions.addVersion(subKey, version)
        }
        else -> getOrCreate(key).version = version
    }

private fun HashMap<String, CodegenVersions>.getOrCreate(name: String): CodegenVersions =
    this[name] ?: CodegenVersions(
        name = name,
        version = null,
        subVersions = hashMapOf(),
    ).also { this[name] = it }

private fun CodegenVersions.toFileSpec(): FileSpec =
    FileSpec.builder("", "Versions")
        .addSuppressWarningTypes()
        .addGradmComment()
        .addVersionsDslProperty()
        .addVersionsObjects(this)
        .build()

private fun FileSpec.Builder.addVersionsObjects(versions: CodegenVersions): FileSpec.Builder =
    apply {
        TypeSpec.objectBuilder("Versions")
            .addSubVersionsProperties(versions)
            .build()
            .also(::addType)
    }

private fun TypeSpec.Builder.addSubVersionsProperties(versions: CodegenVersions): TypeSpec.Builder =
    apply {
        versions.subVersions.toSortedMap().forEach { (name, subVersions) ->
            addVersionProperty(name, subVersions.version)
            addSubVersionsProperty(name, subVersions)
            if (subVersions.subVersions.isNotEmpty()) {
                TypeSpec.objectBuilder("${name.capitalize()}Versions")
                    .addSubVersionsProperties(subVersions)
                    .build()
                    .also(::addType)
            }
        }
    }

private fun TypeSpec.Builder.addVersionProperty(propertyName: String, version: String?): TypeSpec.Builder =
    apply {
        if (version == null) return this
        PropertySpec.builder(propertyName, String::class)
            .addModifiers(KModifier.CONST)
            .initializer("\"$version\"")
            .build()
            .also(::addProperty)
    }

private fun TypeSpec.Builder.addSubVersionsProperty(
    propertyName: String,
    subVersions: CodegenVersions,
): TypeSpec.Builder =
    apply {
        if (subVersions.subVersions.isEmpty()) return this
        PropertySpec.builder(propertyName, ClassName("", "${propertyName.capitalize()}Versions"))
            .initializer("${propertyName.capitalize()}Versions")
            .build()
            .let(::addProperty)
    }

private fun FileSpec.Builder.addVersionsDslProperty(): FileSpec.Builder =
    apply {
        PropertySpec
            .builder("versions", ClassName("", "Versions"))
            .receiver(ClassName("org.gradle.api", "Project"))
            .getter(FunSpec.getterBuilder().addStatement("return Versions").build())
            .build()
            .also(::addProperty)
    }
