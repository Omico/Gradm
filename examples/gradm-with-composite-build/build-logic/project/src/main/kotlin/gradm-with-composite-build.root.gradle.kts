import me.omico.consensus.dsl.requireRootProject

plugins {
    id("gradm-with-composite-build.gradm")
    id("gradm-with-composite-build.root.spotless")
}

requireRootProject()

val wrapper: Wrapper by tasks.named<Wrapper>("wrapper") {
    gradleVersion = versions.gradle
    distributionType = Wrapper.DistributionType.BIN
}
