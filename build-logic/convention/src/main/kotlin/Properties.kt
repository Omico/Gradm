import org.gradle.api.Project
import java.util.Properties

val Project.localProperties: Properties
    get() = Properties().apply {
        val file = rootProject.file("local.properties")
        if (!file.exists()) error("Cannot find local.properties in $rootDir.")
        load(file.inputStream())
    }

fun Project.sensitiveProperty(name: String): String =
    localProperties.getProperty(name)
        ?: System.getenv(name)
        ?: error("Cannot find property $name in local.properties.")

inline infix fun <reified T> Project.property(name: String): T = properties[name] as T
