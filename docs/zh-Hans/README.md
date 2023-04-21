# 简介

![Maven Central](https://img.shields.io/maven-central/v/me.omico.gradm/gradm-runtime)

Gradm (**Gra**dle **d**ependencies **m**anager) 是一种新的简洁并高效的方式来管理依赖。

## 为什么是 Gradm？

Gradle version catalogs 是一个很好的改进。但是也带来了很多问题，而且变得越来越痛苦：

* 生成的代码是完全 Java 的。可用，但是可读性差。
* ~~没有官方的依赖更新支持。~~
* 拥有诸多限制，比如命名：<https://github.com/gradle/gradle/issues/18208>, <https://github.com/gradle/gradle/issues/18201>.
* 或许还有更多。。。？

**Gradm** 就是在这样的背景下诞生的。它拥有优秀的依赖管理和版本更新能力。
