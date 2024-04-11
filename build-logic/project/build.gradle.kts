plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(com.diffplug.spotless)
    implementation(embeddedKotlin("gradle-plugin"))
    implementation(embeddedKotlin("serialization"))
    implementation(gradmGeneratedJar)
    implementation(me.omico.consensus.api)
    implementation(me.omico.consensus.dsl)
    implementation(me.omico.consensus.git)
    implementation(me.omico.consensus.publishing)
    implementation(me.omico.consensus.spotless)
}
