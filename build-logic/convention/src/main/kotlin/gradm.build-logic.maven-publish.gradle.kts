import me.omico.age.dsl.property
import me.omico.age.dsl.sensitiveProperty

plugins {
    `maven-publish`
    signing
}

plugins.withId("java") {
    extensions.configure<JavaPluginExtension> {
        withSourcesJar()
        withJavadocJar()
    }
}

publishing {
    publications.create<MavenPublication>("maven") {
        groupId = project property "POM_GROUP_ID"
        artifactId = project property "POM_ARTIFACT_ID"
        version = project.version.toString()
        from(components["java"])
        pom.configure()
        repositories.configure()
    }
}

signing {
    if (isSnapshot) return@signing
    useGpgCmd()
    sign(publishing.publications["maven"])
}

fun MavenPom.configure() {
    name fromProperty "POM_NAME"
    description fromProperty "POM_DESCRIPTION"
    url fromProperty "POM_URL"
    licenses {
        license {
            name fromProperty "POM_LICENCE_NAME"
            url fromProperty "POM_LICENCE_URL"
        }
    }
    developers {
        developer {
            id fromProperty "POM_DEVELOPER_ID"
            name fromProperty "POM_DEVELOPER_NAME"
        }
    }
    scm {
        connection fromProperty "POM_SCM_CONNECTION"
        developerConnection fromProperty "POM_SCM_DEVELOPER_CONNECTION"
        url fromProperty "POM_SCM_URL"
    }
}

fun RepositoryHandler.configure() {
    maven {
        val urlProperty = when {
            isSnapshot -> "NEXUS_PUBLISH_SNAPSHOT_URL"
            else -> "NEXUS_PUBLISH_RELEASE_URL"
        }
        url = uri(sensitiveProperty(urlProperty))
        credentials {
            username = sensitiveProperty("NEXUS_USERNAME")
            password = sensitiveProperty("NEXUS_PASSWORD")
        }
    }
}

val isSnapshot: Boolean
    get() = version.toString().endsWith("SNAPSHOT")

inline infix fun <reified T> Property<T>.fromProperty(name: String) {
    set(project.property(name) as T)
}
