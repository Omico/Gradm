# 入门

![Maven metadata URL](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Fmaven.omico.me%2Fme%2Fomico%2Fgradm%2Fgradm-gradle-plugin%2Fmaven-metadata.xml)
![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/me.omico.gradm/gradm-gradle-plugin?server=https%3A%2F%2Fs01.oss.sonatype.org)

在教学开始之前，您必须注意 Gradle 的存储库 `(repositories)` 的顺序是有意义的！！！请参阅 [Gradle 的文档](https://docs.gradle.org/current/userguide/declaring_repositories.html#sec:declaring_multiple_repositories)。

在 `settings.gradle.kts` 中，添加以下代码：

```kotlin
pluginManagement {
    includeBuild("gradm") // 在此处加入 Gradm
}

plugins {
    // 你可以通过 Gradm 声明一个特殊的 id
    // 更多细节请查看“自定义”部分
    id("me.omico.gradm.generated")
}
```

在项目的根目录中新建一个名为 `gradm` 的文件夹。

新建 `gradm/settings.gradle.kts`  添加以下内容：

```kotlin
rootProject.name = "gradm"

pluginManagement {
    repositories {
        // 如果您使用快照版本,在顶部声明以下两行。
        mavenLocal()
        maven(url = "https://s01.oss.sonatype.org/content/repositories/snapshots")

        // 自从 3.3.3 版本，Gradm 发布只到我的个人 Maven 仓库。
        maven(url = "https://maven.omico.me")
        mavenCentral()
        gradlePluginPortal()
    }
}
```

在 `gradm` 文件夹中创建一个名为 `gradm.yml` 的文件，并添加以下内容：

```yaml
versions:
  something: "1.0.0" # 声明依赖版本

# 注意 Gradle 的存储库的顺序是有意义的！！！
# 目的是允许 Gradle 正确地获取依赖项。
# 下面的存储库是内置的：
#   google: https://maven.google.com
#   mavenCentral: https://repo1.maven.org/maven2
#   gradlePluginPortal: https://plugins.gradle.org/m2
#   mavenLocal: 在您的主目录中的 .m2 文件夹
#   noUpdates: 未跟踪的存储库
#   omico: https://maven.omico.me
repositories:
  google: # "google" 已经被内置了
  mavenCentral:
  mavenLocal:
  your-repo: # 自定义储存库的 ID
    url: https://repo.example.com # 自定义仓库的 URL
  # Gradm 内置了名为 "noUpdates" 的存储库，它不会更新依赖项的版本。
  # 除去直接使用 "noUpdates"，您也可以像下面这样定义您自己的。
  your-repo-2:
    noUpdates: true # 禁用此储存库的更新

dependencies:
  google:
    androidx.activity:
      activity-compose:
        alias: androidx.activity.compose
        version: ${versions.androidx.activity}
      activity-ktx:
        alias: androidx.activity.ktx
        # 生成的代码将不包含版本。
        # 例如，androidx.activity:activity-ktx。
        noSpecificVersion: true
    androidx.compose:
      # 为了让 Gradm 识别这个依赖是一个 BOM 依赖，
      # 您需要指定如下所示的 "bom: true" 属性。
      # 您不需要添加诸如 "implementation(platform(androidx.compose.bom))" 之类的内容。
      # 注意，如果要使用BOM中定义的版本，
      # 您需要添加 "noSpecificVersion：true"。
      compose-bom:
        alias: androidx.compose.bom
        version: ${versions.androidx.compose.bom}
        bom: true
      # Gradm 将使用 BOM 中定义的版本。
      androidx.compose.animation:
        animation:
          alias: androidx.compose.animation
          noSpecificVersion: true
  noUpdates: # 使用内置储存库 "noUpdates"
    com.example.group:
      noUpdates:
        alias: my.company.noUpdates
        version: ${versions.something}
  your-repo-2:
    com.example.group:
      noUpdates2:
        alias: my.company.noUpdates2
        version: ${versions.something}
  your-repo: # 来自存储库的 ID
    com.example.group:
      example-artifact:
        # 最终使用，例如：compileOnly(my.company.artifact)
        alias: my.company.artifact
        # 您可以使用特定版本，如 "1.0.0" 或使用版本变量。
        # 如果您不指定版本，它将使用最新版本。
        # version: "1.0.0"
        version: ${versions.something}
```

## 自定义

### 自定义插件 ID

您可以在 `gradm/build.gradle.kts` 中修改生成的插件 ID：

```kotlin
gradm {
    pluginId = "gradm" // 默认为 "me.omico.gradm.generated"
}
```

请务必也更改 settings.gradle.kts 中的插件 ID。

### 自定义 Gradm 配置文件名

可以在 `gradm/build.gradle.kts` 中修改 Gradm 的配置文件名：

```kotlin
gradm {
    configFilePath = "gradm3.yml" // 默认为 "gradm.yml"
}
```

### 启用 Gradm 调试模式

您可以在 `gradm/build.gradle.kts` 中启用 Gradm 调试模式：

```kotlin
gradm {
    debug = true // 默认为 false
}
```

### 自定义 Gradm 配置格式

在 `gradm/build.gradle.kts` 中：

```kotlin
gradm {
    format {
        enabled = true // 默认为 true
        indent = 2 // 默认为 2
    }
}
```

### 额外的集成支持

目前，Gradm 支持以下集成：

* GitHub: `github`

在 `gradm/build.gradle.kts` 中：

```kotlin
gradm {
    integrations {
        github {
            // 我们可以暂时禁用集成，而不删除整个配置。
            enabled = true // 默认为 true
            configurationFilePath = "gradm.integration.github.yml" // 默认为 "gradm.integration.github.yml"
        }
    }
}
```

### 启用实验性功能

在 `gradm/build.gradle.kts` 中：

```kotlin
gradm {
    experimental {
        kotlinMultiplatformSupport = true // 默认为 false
    }
}
```
