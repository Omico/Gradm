package me.omico.gradm.internal.codegen

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import me.omico.gradm.GRADM_DEPENDENCY_PACKAGE_NAME
import me.omico.gradm.GradmDependency
import me.omico.gradm.internal.VersionsMeta
import me.omico.gradm.internal.config.Library
import me.omico.gradm.internal.config.alias
import java.util.Locale

internal data class CodegenDependency(
    val name: String,
    val libraries: ArrayList<CodegenLibrary>,
    val subDependencies: HashMap<String, CodegenDependency>,
)

internal fun CodegenDependency.addLibrary(
    dependencyName: String,
    library: Library,
    versionsMeta: VersionsMeta,
) {
    when {
        !library.alias().contains(".") -> libraries.add(library.toCodegenLibrary(versionsMeta))
        else -> {
            val strings = library.alias().split(".")
            val subName = strings.first()
            val subAlias = strings.drop(1).joinToString(".")
            val subDependencyName = "${dependencyName.capitalize()}${subName.capitalize()}"
            subDependencies
                .getOrCreate(subName, subDependencyName)
                .addLibrary(
                    dependencyName = subDependencyName,
                    library = library.copy(alias = subAlias),
                    versionsMeta = versionsMeta,
                )
        }
    }
}

internal fun HashMap<String, CodegenDependency>.getOrCreate(
    key: String,
    dependencyName: String = key,
): CodegenDependency =
    this[key] ?: CodegenDependency(
        name = dependencyName,
        libraries = arrayListOf(),
        subDependencies = hashMapOf(),
    ).also { this[key] = it }

internal fun CodegenDependency.toFileSpec(): FileSpec =
    FileSpec.builder(GRADM_DEPENDENCY_PACKAGE_NAME, name)
        .addSuppressWarningTypes()
        .addGradmComment()
        .apply { createDependencyObjects(this@toFileSpec) }
        .build()

internal fun List<CodegenDependency>.toDslFileSpec(): FileSpec =
    FileSpec.builder("", "Dependencies")
        .addSuppressWarningTypes()
        .addGradmComment()
        .apply {
            forEach { dependency ->
                addDslProperty(
                    propertyName = dependency.name,
                    receivers = arrayOf(
                        ClassName("org.gradle.api.artifacts.dsl", "DependencyHandler"),
                        ClassName("org.jetbrains.kotlin.gradle.plugin", "KotlinDependencyHandler"),
                    )
                )
            }
        }
        .build()

private fun FileSpec.Builder.createDependencyObjects(dependency: CodegenDependency) {
    TypeSpec.objectBuilder(dependency.name).addSuperinterface(GradmDependency::class)
        .apply { dependency.libraries.forEach(::addLibrary) }
        .apply { dependency.subDependencies.forEach { addSubDependencyProperty(it.key, it.value.name) } }
        .build()
        .also(::addType)
    dependency.subDependencies.values.sortedBy { it.name }.forEach(::createDependencyObjects)
}

private fun TypeSpec.Builder.addSubDependencyProperty(propertyName: String, dependencyName: String): TypeSpec.Builder =
    PropertySpec.builder(propertyName, ClassName(GRADM_DEPENDENCY_PACKAGE_NAME, dependencyName))
        .initializer(dependencyName)
        .build()
        .let(::addProperty)

private fun FileSpec.Builder.addDslProperty(propertyName: String, receivers: Array<ClassName>): FileSpec.Builder =
    apply {
        receivers.forEach { className ->
            PropertySpec
                .builder(
                    propertyName.lowercase(Locale.getDefault()),
                    ClassName(GRADM_DEPENDENCY_PACKAGE_NAME, propertyName)
                )
                .receiver(className)
                .getter(
                    FunSpec.getterBuilder()
                        .addStatement("return $propertyName", ClassName(GRADM_DEPENDENCY_PACKAGE_NAME, propertyName))
                        .build()
                )
                .build()
                .also(::addProperty)
        }
    }
