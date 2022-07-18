package me.omico.gradm.utility

import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import kotlin.io.path.isDirectory

fun Path.deleteDirectory() {
    if (exists() && isDirectory()) Files.walk(this).sorted(Comparator.reverseOrder()).forEach(Files::delete)
}

fun Path.clearDirectory() {
    deleteDirectory()
    createDirectories()
}
