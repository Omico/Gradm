plugins {
    kotlin("jvm")
    id("gradm.publishing")
}

kotlin {
    jvmToolchain(11)
    sourceSets {
        main {
            kotlin.srcDir("$buildDir/generated/sources/kotlinTemplates")
        }
    }
}

dependencies {
    api(gradleApi())
    implementation(libs.snakeyaml)
    implementation(libs.kotlinx.coroutines)
}

dependencies {
    api("me.omico.gradm:gradm-api-datastore:$version")
}

dependencies {
    testImplementation("me.omico.gradm:gradm-api-test:$version")
}

tasks.test {
    useJUnitPlatform()
}

val copyKotlinTemplates by tasks.registering(Copy::class) {
    from("src/main/kotlinTemplates")
    into("$buildDir/generated/sources/kotlinTemplates")
    expand("version" to version)
    filteringCharset = Charsets.UTF_8.toString()
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

tasks.compileKotlin {
    dependsOn(copyKotlinTemplates)
}

val sourcesJar by tasks.named("sourcesJar") {
    dependsOn(copyKotlinTemplates)
}
