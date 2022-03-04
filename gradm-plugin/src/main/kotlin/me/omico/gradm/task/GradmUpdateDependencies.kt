package me.omico.gradm.task

import me.omico.gradm.GradmParser
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.work.DisableCachingByDefault

@DisableCachingByDefault(because = "Not worth caching")
abstract class GradmUpdateDependencies : DefaultTask() {
    @TaskAction
    fun execute() = GradmParser.execute()
}
