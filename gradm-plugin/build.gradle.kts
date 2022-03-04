import me.omico.age.dsl.javaCompatibility
import me.omico.age.dsl.withKotlinMavenPublication
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
}

javaCompatibility(all = JavaVersion.VERSION_11)
withKotlinMavenPublication(mavenPublicationName = "gradlePlugin")

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "11"
    }
}

gradlePlugin {
    plugins {
        register("gradm") {
            id = "me.omico.gradm"
            implementationClass = "me.omico.gradm.GradmPlugin"
        }
    }
}

dependencies {
    compileOnly(gradleApi())
    compileOnly(gradleKotlinDsl())
    implementation(project(":gradm-runtime"))
}
