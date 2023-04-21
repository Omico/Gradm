# 在复合构建中使用

![Maven Central](https://img.shields.io/maven-central/v/me.omico.gradm/gradm-runtime)

**假设你已经阅读[入门](./getting-started).**

在您的根目录中创建一个名为 `build-logic` 的文件夹。

创建 `build-logic/settings.gradle.kts`。 内容与[入门](./getting-started)中的 `settings.gradle.kts` 完全一致。

将[入门](./getting-started)中的 `gradm` 文件夹复制到 `build-logic`。

在 `settings.gradle.kts` 中，添加以下内容：

```kotlin
pluginManagement {
    includeBuild("build-logic")
    includeBuild("build-logic/gradm")
}

plugins {
    id("<gradm generated plugin id>") // 默认是 "me.omico.gradm.generated"
}
```

## 自定义

如果你想自定义一些 *.gradle.kts 脚本，你可能想直接使用版本或依赖。

假设您已经将一些脚本定义到 "convention" 模块中。

您可以通过将以下内容添加到 `convention\build.gradle.kts` 来做到这一点：

```kotlin
dependencies {
    implementation(gradmGeneratedJar)
}
```

你可以在[build-logic.android.library.gradle.kts](https://github.com/Omico/Gradm/blob/release/examples/gradm-with-composite-build/build-logic/convention/src/main/kotlin/build-logic.android.library.gradle.kts)
中看到一个实际的例子。
