package autoUpdate

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.diff.DiffEntry
import java.io.File

val git = Git.open(File("ZenXSin/"))
val diffCommand = git.diff()
val diffEntries = diffCommand.call()

for (entry in diffEntries) {
// 处理文件差异
}