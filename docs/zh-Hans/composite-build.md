# 在复合构建中使用

![Maven metadata URL](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Fmaven.omico.me%2Fme%2Fomico%2Fgradm%2Fgradm-gradle-plugin%2Fmaven-metadata.xml)
![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/me.omico.gradm/gradm-gradle-plugin?server=https%3A%2F%2Fs01.oss.sonatype.org)

**假设你已经阅读[入门](./getting-started).**

在你的根目录下创建 `build-logic/project` 文件夹。

新建一个 `settings.gradle.kts` 到 `build-logic/project/settings.gradle.kts`，并添加以下内容：

```kotlin
pluginManagement {
    includeBuild("build-logic/gradm")
}

plugins {
    id("<gradm generated plugin id>") // 默认是 "me.omico.gradm.generated"
}
```

从[入门](./getting-started)复制 `gradm` 文件夹到 `build-logic/gradm`。

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
