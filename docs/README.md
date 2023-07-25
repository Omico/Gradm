# Introduction

![Maven metadata URL](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Fmaven.omico.me%2Fme%2Fomico%2Fgradm%2Fgradm-gradle-plugin%2Fmaven-metadata.xml)
![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/me.omico.gradm/gradm-gradle-plugin?server=https%3A%2F%2Fs01.oss.sonatype.org)

Gradm (**Gra**dle **d**ependencies **m**anager) is a new way to manage dependencies easier and more efficient.

## Why Gradm?

The Gradle version catalogs is an excellent move for us. But it also brings a lot of problems, and becomes more and more painful:

* The Generated code is fully Java, with very poor readability.
* ~~No official dependencies update support.~~
* Terrible restrictions, like naming: <https://github.com/gradle/gradle/issues/18208>, <https://github.com/gradle/gradle/issues/18201>.
* And more...?

**Gradm** was born for the above problems. It has an excellent ability of dependencies management and versions update:

* The Generated code is fully Kotlin and can quickly obtain dependency information through code jumps.
* Provides the same usage as the Gradle version catalog and removes naming restrictions.
* Provide dependency update support. (Will appear in Gradle build output, if updates exist.)
* Less configuration code, no need configure repositories in gradle files.
* More features are under development...

Want to know more about what the Gradm generated code plugin does, please check me.omico.gradm.generated.GradmPlugin for more information.
