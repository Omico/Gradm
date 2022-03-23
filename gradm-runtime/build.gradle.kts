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
    implementation("org.snakeyaml:snakeyaml-engine:2.3")
    implementation("com.squareup:kotlinpoet:1.10.2")
    @Suppress("GradlePackageUpdate") // Keep version compatible with built-in kotlin version.
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")
}

val copyKotlinTemplates by tasks.registering(Copy::class) {
    from("src/main/kotlinTemplates")
    into("$buildDir/generated/sources/kotlinTemplates")
    expand("version" to version)
    filteringCharset = Charsets.UTF_8.toString()
}

tasks.withType<KotlinCompile> {
    dependsOn(copyKotlinTemplates)
}
