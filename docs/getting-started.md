# Getting Started

![Maven metadata URL](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Fmaven.omico.me%2Fme%2Fomico%2Fgradm%2Fgradm-gradle-plugin%2Fmaven-metadata.xml)
![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/me.omico.gradm/gradm-gradle-plugin?server=https%3A%2F%2Fs01.oss.sonatype.org)

Before the guidance, you must notice that the order of Gradle's repositories is matter!!! See also [Gradle's documentation](https://docs.gradle.org/current/userguide/declaring_repositories.html#sec:declaring_multiple_repositories).

In `settings.gradle.kts`, add the following:

```kotlin
pluginManagement {
    includeBuild("gradm") // include Gradm here
}

plugins {
    // you can declare a special id via Gradm
    // more details are in the "Customizing" section
    id("me.omico.gradm.generated")
}
```

Create a folder named `gradm` in the root directory of your project.

Create `gradm/settings.gradle.kts`, and add the following:

```kotlin
rootProject.name = "gradm"

pluginManagement {
    repositories {
        // Declare the below two lines at the top, if you use the snapshot version.
        mavenLocal()
        maven(url = "https://s01.oss.sonatype.org/content/repositories/snapshots")

        // Since 3.3.3 version, Gradm is only publish to my personal Maven repository.
        maven(url = "https://maven.omico.me")
        mavenCentral()
        gradlePluginPortal()
    }
}
```

Create a file named `gradm.yml` in `gradm` folder, and add the following:

```yaml
versions:
  something: "1.0.0" # create your own version

# Note that the order of repositories is matter!!!
# The purpose is to allow Gradle to correctly obtain dependencies.
# The following repositories are build-in:
#   google: https://maven.google.com
#   mavenCentral: https://repo1.maven.org/maven2
#   gradlePluginPortal: https://plugins.gradle.org/m2
#   mavenLocal: .m2 folder in your home directory
#   noUpdates: untracked repository
#   omico: https://maven.omico.me
repositories:
  google: # a repo named "google" is already build-in.
  mavenCentral:
  mavenLocal:
  your-repo: # your own repo id
    url: https://repo.example.com # your repo url
  # Instead of using "noUpdates" directly, you can also define your own like below.
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
    configurationFilePath = "gradm3.yml" // default: "gradm.yml"
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
        github {
            // We can temporarily disable the integration, without delete the whole configuration.
            enabled = true // default: true
            configurationFilePath = "gradm.integration.github.yml" // default: "gradm.integration.github.yml"
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
    }
}
```
