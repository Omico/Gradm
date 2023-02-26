package me.omico.build

import java.io.File

inline val File.gitPreCommitHook
    get() = File(this, ".git/hooks/pre-commit")
