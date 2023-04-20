plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(com.android.library)
    implementation(com.diffplug.spotless)
    implementation(gradmGeneratedJar)
    implementation(me.omico.age.spotless)
    implementation(org.jetbrains.kotlin.android)
}
