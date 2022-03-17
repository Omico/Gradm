package me.omico.gradm.internal.codegen

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import me.omico.gradm.GRADM_DEPENDENCY_PACKAGE_NAME
import me.omico.gradm.GradmDependency
import java.util.Locale

internal data class Dependency(
    val name: String,
    val libraries: ArrayList<Library>,
    val subDependencies: HashMap<String, Dependency>,
)

internal fun Dependency.toFileSpec(): FileSpec =
    FileSpec.builder(GRADM_DEPENDENCY_PACKAGE_NAME, name)
        .addSuppressWarningTypes()
        .addGradmComment()
        .apply { createDependencyObjects(this@toFileSpec) }
        .build()

internal fun List<Dependency>.toDslFileSpec(): FileSpec =
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

private fun FileSpec.Builder.createDependencyObjects(dependency: Dependency) {
    TypeSpec.objectBuilder(dependency.name).addSuperinterface(GradmDependency::class)
        .apply { dependency.libraries.forEach(::addLibrary) }
        .apply { dependency.subDependencies.forEach { addSubDependencyProperty(it.key, it.value.name) } }
        .build()
        .also(::addType)
    dependency.subDependencies.values.forEach(::createDependencyObjects)
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
