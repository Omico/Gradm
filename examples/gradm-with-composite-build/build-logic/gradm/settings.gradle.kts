rootProject.name = "gradm-with-composite-build-gradm"

pluginManagement {
    repositories {
        mavenLocal()
        maven(url = "https://s01.oss.sonatype.org/content/repositories/snapshots")
        maven(url = "https://maven.omico.me")
        mavenCentral()
        gradlePluginPortal()
    }
}
