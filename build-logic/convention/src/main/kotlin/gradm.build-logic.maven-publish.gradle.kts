plugins {
    id("me.omico.age.project.maven-publish")
}

extensions.findByType<JavaPluginExtension>()?.apply {
    withSourcesJar()
    withJavadocJar()
}

extensions.findByType<GradlePluginDevelopmentExtension>()?.apply {
    isAutomatedPublishing = false
}
