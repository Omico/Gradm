# Gradm

![Maven Central](https://img.shields.io/maven-central/v/me.omico.gradm/gradm-runtime)

Gradm (**Gra**dle **d**ependencies **m**anager) is a new way to manage dependencies easier and more efficient.

## Why Gradm?

The Gradle version catalogs is an excellent move for us. But it also brings a lot of problems, and becomes more and more painful:

* The Generated code is fully Java. Usable, and that's it. Not good for reading.
* No official dependencies update support.
* Terrible restrictions, like naming: <https://github.com/gradle/gradle/issues/18208>, <https://github.com/gradle/gradle/issues/18201>.
* And more...?

**Gradm** was born for the above problems. It has an excellent ability of dependencies management and versions update.

## Getting Started

In settings.gradle.kts, add the following:

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
    url: https://maven.google.com
  your-repo: # your own repo id
    url: https://repo.example.com # your repo url

dependencies:
  google:
    androidx.activity:
      activity-compose:
        alias: androidx.activity.compose
        version: ${versions.androidx.activity}
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

For more information, please see the [example project](../../tree/release/example).

## License

```txt
Copyright 2022 Omico

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
