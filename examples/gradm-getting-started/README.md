# Gradm Getting Started

![Maven Central](https://img.shields.io/maven-central/v/me.omico.gradm/gradm-runtime)

In `settings.gradle.kts`, add the following:

```kotlin
pluginManagement {
    repositories {
        mavenCentral()
    }
    plugins {
        id("me.omico.gradm") version "<version>"
    }
}

plugins {
    id("me.omico.gradm")
}

gradm {
    configs {
        debug = true // default: false
        format = true // default: false
        indent = 4 // default: 2, won't take effect if format is false.
    }
}
```

Create a file named `gradm.yml` in your root project directory, and add the following:

```yaml
versions:
  something: "1.0.0" # create your own version

repositories:
  google:
    url: "https://maven.google.com"
  your-repo: # your own repo id
    url: "https://repo.example.com" # your repo url
  # A repo named "noUpdates" is already build-in.
  # You can use it directly or define your own like below.
  your-repo-2:
    noUpdates: true # disable updates for this repo

dependencies:
  google:
    androidx.activity:
      activity-compose:
        alias: androidx.activity.compose
        version: ${versions.androidx.activity}
      activity-ktx:
        alias: androidx.activity.ktx
        # Generated code won't contain version.
        # For example, androidx.activity:activity-ktx.
        noSpecificVersion: true
  noUpdates: # use build-in repo "noUpdates"
    com.example.group:
      noUpdates:
        alias: my.company.noUpdates
        version: ${versions.something}
  your-repo-2:
    com.example.group:
      noUpdates2:
        alias: my.company.noUpdates2
        version: ${versions.something}
  your-repo: # from repositories' id
    com.example.group:
      example-artifact:
        # for final use, for example:
        # compileOnly(my.company.artifact)
        alias: my.company.artifact
        # You can use either specific version like "1.0.0" or use version variables.
        # It will use the latest version, if you don't specify version.
        # version: "1.0.0"
        version: ${versions.something}
```
