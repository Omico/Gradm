import me.omico.age.dsl.findSensitiveProperty
import me.omico.age.dsl.property
import me.omico.age.dsl.sensitiveProperty

plugins {
    `maven-publish`
    signing
}

extensions.configure<PublishingExtension> {
    repositories.configure()
    publications.create<MavenPublication>("maven") {
        groupId = project property "PROJECT_GROUP_ID"
        artifactId = project property "PROJECT_ARTIFACT_ID"
        version = project property "PROJECT_VERSION"
        from(components["java"])
        pom.configure()
    }
}

extensions.configure<SigningExtension> {
    if (isSnapshot) return@configure
    useGpgCmd()
    sign(extensions.getByType<PublishingExtension>().publications["maven"])
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
    val urlProperty = when {
        isSnapshot -> "NEXUS_PUBLISH_SNAPSHOT_URL"
        else -> "NEXUS_PUBLISH_RELEASE_URL"
    }
    val aUrl = findSensitiveProperty(urlProperty)
    if (aUrl == null) {
        logger.warn("$urlProperty is not set. Currently cannot publish to Nexus.")
        return
    }
    maven {
        url = uri(aUrl)
        credentials {
            username = sensitiveProperty("NEXUS_USERNAME")
            password = sensitiveProperty("NEXUS_PASSWORD")
        }
    }
}

val isSnapshot: Boolean
    get() = project.property<String>("PROJECT_VERSION").endsWith("SNAPSHOT")

inline infix fun <reified T> Property<T>.fromProperty(name: String) {
    set(project.property(name) as T)
}

extensions.findByType<JavaPluginExtension>()?.apply {
    withSourcesJar()
    withJavadocJar()
}

extensions.findByType<GradlePluginDevelopmentExtension>()?.apply {
    isAutomatedPublishing = false
}
