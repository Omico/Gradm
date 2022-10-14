import me.omico.age.spotless.configureSpotless
import me.omico.age.spotless.kotlin
import me.omico.age.spotless.kotlinGradle

plugins {
    id("com.diffplug.spotless")
    id("me.omico.age.spotless")
}

when (project) {
    rootProject -> allprojects { configureSpotless() }
    else -> configureSpotless()
}

fun Project.configureSpotless() {
    configureSpotless {
        kotlin()
        kotlinGradle()
    }
}
