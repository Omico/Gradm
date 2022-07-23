import me.omico.age.dsl.withKotlinMavenPublication

plugins {
    `embedded-kotlin`
}

withKotlinMavenPublication()

kotlin {
    target.compilations.all {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }
    sourceSets["main"].kotlin.srcDir("$buildDir/generated/sources/kotlinTemplates")
}

dependencies {
    compileOnly(gradleApi())
    implementation(libs.snakeyaml)
    implementation(libs.kotlinx.coroutines)
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.8.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
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
