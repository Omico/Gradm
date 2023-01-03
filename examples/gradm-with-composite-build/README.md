# Gradm with composite build

![Maven Central](https://img.shields.io/maven-central/v/me.omico.gradm/gradm-runtime)

**Assuming you have read [getting-started](../gradm-getting-started).**

Create a folder named `build-logic` in your root directory.

Create `build-logic/settings.gradle.kts`. The content is exactly the same as `settings.gradle.kts` in [getting-started](../gradm-getting-started).

Copy `gradm` folder from [getting-started](../gradm-getting-started) to `build-logic`.

In `settings.gradle.kts`, add the following:

```kotlin
pluginManagement {
    includeBuild("build-logic")
    includeBuild("build-logic/gradm")
}

plugins {
    id("<gradm generated plugin id>") // default is "me.omico.gradm.generated"
}
```

## Customizing

If you want to customize some *.gradle.kts scripts, you may want to use versions or dependencies directly.

Assuming you have defined some scripts into `converntion` module.

You can do this by adding the following to `converntion\build.gradle.kts`:

```kotlin
dependencies {
    implementation(gradmGeneratedJar)
}
```

You can see a actual example in [build-logic.android.library.gradle.kts](build-logic/convention/src/main/kotlin/build-logic.android.library.gradle.kts).
