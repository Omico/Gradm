import org.gradle.internal.os.OperatingSystem
import java.nio.file.Files

plugins {
    id("gradm.build-logic.root-project.base")
}

installGitHooks()

fun Project.installGitHooks() {
    val rootDir = rootProject.rootDir
    val target = File(rootDir, ".git/hooks")
    val source = File(rootDir, ".git-hooks")
    if (target.canonicalFile == source) return
    target.deleteRecursively()
    when {
        OperatingSystem.current().isWindows -> source.copyRecursively(target)
        else -> Files.createSymbolicLink(target.toPath(), source.toPath())
    }
}
