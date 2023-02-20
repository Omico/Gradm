plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(gradmGeneratedJar)
    implementation(project(":typesafe"))
}

dependencies {
    implementation(com.android.library)
    implementation(com.diffplug.spotless)
    implementation(me.omico.age.spotless)
}
