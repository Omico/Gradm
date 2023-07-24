import me.omico.consensus.dsl.consensus
import me.omico.consensus.dsl.requireRootProject

plugins {
    id("com.diffplug.spotless")
    id("me.omico.consensus.spotless")
}

requireRootProject()

consensus {
    spotless {
        freshmark()
        gradleProperties()
        kotlin()
        kotlinGradle()
    }
}
