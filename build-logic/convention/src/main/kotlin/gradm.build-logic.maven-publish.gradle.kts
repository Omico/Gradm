plugins {
    id("me.omico.age.project.maven-publish")
}

plugins.withId("java") {
    extensions.findByType<GradlePluginDevelopmentExtension>()?.apply {
        isAutomatedPublishing = false
    }
}
