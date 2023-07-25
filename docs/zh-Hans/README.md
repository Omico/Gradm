# 简介

![Maven metadata URL](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Fmaven.omico.me%2Fme%2Fomico%2Fgradm%2Fgradm-gradle-plugin%2Fmaven-metadata.xml)
![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/me.omico.gradm/gradm-gradle-plugin?server=https%3A%2F%2Fs01.oss.sonatype.org)

Gradm (**Gra**dle **d**ependencies **m**anager) 是一种新的简洁并高效的方式来管理依赖。

## 为什么是 Gradm？

Gradle version catalogs 是一个很好的改进。但是也带来了很多问题，而且变得越来越痛苦：

* 生成的代码是完全 Java 的。可用，但是可读性极差。
* ~~没有官方的依赖更新支持。~~
* 拥有诸多限制，比如命名：<https://github.com/gradle/gradle/issues/18208>, <https://github.com/gradle/gradle/issues/18201>.
* 或许还有更多。。。？

**Gradm** 就是在这样的背景下诞生的。它拥有优秀的依赖管理和版本更新能力：

* 生成的代码是完全 Kotlin 的，并且可以通过代码跳转快速获取依赖信息。
* 提供了与 Gradle version catalogs 相同的使用方式，并且移除了命名限制。
* 提供了依赖更新支持。（如果存在更新，将会出现在 Gradle 构建输出中）
* 更少的配置代码，不需要在 Gradle 文件中配置仓库（repositories）。
* 更多功能正在开发中。。。

想知道更多关于 Gradm 生成的代码插件做了什么，请查看 me.omico.gradm.generated.GradmPlugin 了解更多信息。
