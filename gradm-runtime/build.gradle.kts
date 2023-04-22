plugins {
    `embedded-kotlin`
    id("gradm.build-logic.maven-publish")
}

kotlin {
    jvmToolchain(11)
    sourceSets["main"].kotlin.srcDir("$buildDir/generated/sources/kotlinTemplates")
}

dependencies {
    api(gradleApi())
    implementation(libs.snakeyaml)
    implementation(libs.kotlinx.coroutines)
}

dependencies {
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
}

tasks.test {
    useJUnitPlatform()
}

val copyKotlinTemplates by tasks.registering(Copy::class) {
    from("src/main/kotlinTemplates")
    into("$buildDir/generated/sources/kotlinTemplates")
    expand("version" to properties["PROJECT_VERSION"])
    filteringCharset = Charsets.UTF_8.toString()
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

tasks.compileKotlin {
    dependsOn(copyKotlinTemplates)
}

val sourcesJar by tasks.named("sourcesJar") {
    dependsOn(copyKotlinTemplates)
}
