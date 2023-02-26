plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(com.diffplug.spotless)
    implementation(gradmGeneratedJar)
    implementation(me.omico.age.dsl)
    implementation(me.omico.age.spotless)
}
