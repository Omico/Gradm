plugins {
    `kotlin-dsl`
}

configurations.all {
    resolutionStrategy.cacheChangingModulesFor(0, TimeUnit.SECONDS)
}

repositories {
    mavenCentral()
    maven(url = "https://s01.oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    val versions = object {
        val age = "1.0.0-SNAPSHOT"
    }
    implementation("me.omico.age.settings:me.omico.age.settings.gradle.plugin:${versions.age}")
    implementation("me.omico.age:age-dsl:${versions.age}")
}
