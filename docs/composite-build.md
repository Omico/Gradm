# Use in composite build

![Maven Central](https://img.shields.io/maven-central/v/me.omico.gradm/gradm-runtime)

**Assuming you have read [getting-started](./getting-started).**

Create a `build-logic/project` folder in your root path.

Create a new `settings.gradle.kts` to `build-logic/project/settings.gradle.kts`, add the following:

```kotlin
pluginManagement {
    includeBuild("build-logic/gradm")
}

plugins {
    id("<gradm generated plugin id>") // default is "me.omico.gradm.generated"
}
```

Copy `gradm` folder from [getting-started](./getting-started) to `build-logic/gradm`.

## Customizing

If you want to customize some *.gradle.kts scripts, you may want to use versions or dependencies directly.

Assuming you have defined some scripts into `build-logic/project/main/src/kotlin` module.

You can do this by adding the following to `build-logic/project/build.gradle.kts`:

```kotlin
dependencies {
    implementation(gradmGeneratedJar)
}
```

You can see a actual example in [build-logic.android.library.gradle.kts](https://github.com/Omico/Gradm/blob/release/examples/gradm-with-composite-build/build-logic/convention/src/main/kotlin/build-logic.android.library.gradle.kts).
