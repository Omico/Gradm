# 入门

![Maven Central](https://img.shields.io/maven-central/v/me.omico.gradm/gradm-runtime)

在 `settings.gradle.kts` 中，添加以下代码：

```kotlin
pluginManagement {
    includeBuild("gradm") // 在此处加入 Gradm
    repositories {
        gradlePluginPortal {
            content {
                excludeGroupByRegex("me.omico.*") // 减少构建时间
            }
        }
        mavenCentral()
        mavenLocal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        mavenLocal()
    }
}

plugins {
    // 你可以通过 Gradm 声明一个特殊的 id
    // 更多细节请查看“自定义”部分
    id("me.omico.gradm.generated")
}
```

在项目的根目录中创建一个名为 `gradm` 的文件夹，并创建 `gradm/settings.gradle.kts`  添加以下内容：

```kotlin
rootProject.name = "gradm"

pluginManagement {
    repositories {
        mavenCentral()
        mavenLocal()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        // 以下存储库与您为 Gradm 声明的插件相关
        google()
        mavenCentral()
        mavenLocal()
    }
}

```

在 `gradm` 文件夹中创建一个名为 `gradm.yml` 的文件，并添加以下内容：

```yaml
versions:
  something: "1.0.0" # 声明依赖版本

repositories:
  google:
    url: https://maven.google.com
  your-repo: # 自定义储存库的 ID
    url: https://repo.example.com # 自定义仓库的 URL
  # Gradm 内置了名为 "noUpdates" 的存储库，它不会更新依赖项的版本。
  # 您可以直接使用它，也可以像下面这样定义您自己的。
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
        apply("github") {
            // 我们可以暂时禁用集成，而不删除整个配置。
            enabled = true // 默认为 true
            configFilePath = "gradm.integration.github.yml" // 默认为 "gradm.integration.github.yml"
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
