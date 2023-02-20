# Gradm Getting Started

![Maven Central](https://img.shields.io/maven-central/v/me.omico.gradm/gradm-runtime)

In `settings.gradle.kts`, add the following:

```kotlin
pluginManagement {
    includeBuild("gradm") // include Gradm here
    repositories {
        gradlePluginPortal {
            content {
                excludeGroupByRegex("me.omico.*") // reduce build time
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
    // you can declare a special id via Gradm
    // more details are in the "Customizing" section
    id("me.omico.gradm.generated")
}
```

Create a folder named `gradm` in the root directory of your project, and create `gradm/settings.gradle.kts`. Add the following:

```kotlin
rootProject.name = "gradm"

pluginManagement {
    repositories {
        gradlePluginPortal {
            content {
                excludeGroupByRegex("me.omico.*") // reduce build time
            }
        }
        mavenCentral() // for Gradm
        mavenLocal() // only for snapshot
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        // following repositories are related to which plugins you declared for Gradm
        google()
        mavenCentral()
        mavenLocal()
    }
}

```

Create a file named `gradm.yml` in `gradm` folder, and add the following:

```yaml
versions:
  something: "1.0.0" # create your own version

repositories:
  google:
    url: https://maven.google.com
  your-repo: # your own repo id
    url: https://repo.example.com # your repo url
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
    androidx.compose:
      # To let Gradm recognize this dependency is a BOM dependency,
      # you need to specify a "bom: true" property like the one below.
      # You don't need to add something like "implementation(platform(androidx.compose.bom))".
      # Note that if you want to use the version defined in BOM,
      # you need to add "noSpecificVersion: true".
      compose-bom:
        alias: androidx.compose.bom
        version: ${versions.androidx.compose.bom}
        bom: true
      # Gradm will use the version defined in BOM.
      androidx.compose.animation:
        animation:
          alias: androidx.compose.animation
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

## Customizing

### Customizing the plugin id

You can modify the generated plugin id in `gradm/build.gradle.kts`:

```kotlin
gradm {
    pluginId = "gradm" // default is "me.omico.gradm.generated"
}
```

Be sure to change the plugin id in `settings.gradle.kts` too.

### Customizing the Gradm configuration file name

You can modify the Gradm configuration file name in `gradm/build.gradle.kts`:

```kotlin
gradm {
    configFilePath = "gradm3.yml" // default: "gradm.yml"
}
```

### Enabling Gradm debug mode

You can enable Gradm debug mode in `gradm/build.gradle.kts`:

```kotlin
gradm {
    debug = true // default: false
}
```

### Customizing the Gradm configuration formatting

In `gradm/build.gradle.kts`:

```kotlin
gradm {
    format {
        enabled = true // default: true
        indent = 2 // default: 2
    }
}
```

### Additional integrations support

Currently, Gradm supports the following integrations:

* Github: `github`

In `gradm/build.gradle.kts`:

```kotlin
gradm {
    integrations {
        apply("github") {
            // We can temporarily disable the integration, without delete the whole configuration.
            enabled = true // default: true
            configFilePath = "gradm.integration.github.yml" // default: "gradm.integration.github.yml"
        }
    }
}
```

### Enable experimental features

In `gradm/build.gradle.kts`:

```kotlin
gradm {
    experimental {
        kotlinMultiplatformSupport = true // default: false
        typesafeProjectAccessorsSubstitution = true // default: false
    }
}
```
