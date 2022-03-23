# Gradm

![Maven Central](https://img.shields.io/maven-central/v/me.omico.gradm/gradm-runtime)

Gradm (**Gra**dle **d**ependencies **m**anager) is a new way to manage dependencies easier and more efficient.

## Why Gradm?

The Gradle version catalog is an excellent move for us. But it also brings a lot of problems, and becomes more and more painful:

* The Generated code is fully Java. Useable, and that's it. Not good for reading.
* Terrible restrictions on naming, see [here](https://github.com/gradle/gradle/issues/18208).
* No official dependencies update support.
* And more...?

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
```

Create a file named `gradm.yml` in your root project directory, and add the following

```yaml
gradm:
  rule-version: 1 # Gradm rule version, currently has no effect on this
  format: true # default is false, if you haven't specify
  indent: 4 # default is 2, won't take effect if format is false

versions:
  something: "1.0.0" # create your own version

repositories:
  - id: google
    url: https://maven.google.com/
  - id: your-repo # your own repo id
    url: https://repo.example.com/ # your repo url

dependencies:
  - name: Libs # create your own dependency name
    repository: google # from repositories' id
    libraries:
      - # for final use, libs.company.artifact
        module: my.company.lib:artifact # create your own library
        alias: company.artifact # create your own alias, optional
        # You can use either specific version like "1.0.0" or use version variables.
        # It will use the latest version, if you don't specify version.
        # version: "1.0.0"
        version: ${versions.something}
      - # This style is also acceptable
        # note: if your enable format, it will be override by the "module" style above
        group: my.company.lib
        artifact: artifact2
        version: "1.0.0"
        alias: company.artifact2
```

For more information, please see the [example project](./example).

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
