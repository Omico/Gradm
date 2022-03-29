import me.omico.age.dsl.javaCompatibility
import me.omico.age.dsl.withKotlinMavenPublication
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `embedded-kotlin`
}

javaCompatibility(all = JavaVersion.VERSION_11)
withKotlinMavenPublication()

kotlin {
    target.compilations.all {
        kotlinOptions {
            jvmTarget = "11"
        }
    }
    sourceSets["main"].kotlin.srcDir("$buildDir/generated/sources/kotlinTemplates")
}

dependencies {
    compileOnly(gradleApi())
    implementation(libs.snakeyaml)
    implementation(libs.kotlinx.coroutines)
}

val copyKotlinTemplates by tasks.registering(Copy::class) {
    from("src/main/kotlinTemplates")
    into("$buildDir/generated/sources/kotlinTemplates")
    expand("version" to version)
    filteringCharset = Charsets.UTF_8.toString()
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

tasks.withType<KotlinCompile> {
    dependsOn(copyKotlinTemplates)
}

val sourcesJar by tasks.named("sourcesJar") {
    dependsOn(copyKotlinTemplates)
}
