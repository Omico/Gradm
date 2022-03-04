package me.omico.gradm.internal.config

import me.omico.gradm.internal.YamlDocument
import me.omico.gradm.internal.YamlObject
import me.omico.gradm.internal.require

typealias Gradm = YamlObject

private val YamlDocument.gradm: Gradm
    get() = require("gradm")

val YamlDocument.gradmVersion: String
    get() = gradm.require("version")

val YamlDocument.gradmRuleVersion: Int
    get() = gradm.require("rule-version")
