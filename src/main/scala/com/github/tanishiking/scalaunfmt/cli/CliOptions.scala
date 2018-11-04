package com.github.tanishiking.scalaunfmt.cli

import com.github.tanishiking.scalaunfmt.config.ScalaUnfmtConfig
import org.scalafmt.cli.{FileFetchMode, GitFiles, InputMethod, RecursiveSearch}
import org.scalafmt.config.{FilterMatcher, ProjectFiles}
import org.scalafmt.util.{AbsoluteFile, FileOps, GitOps, GitOpsImpl, OsSpecific}

case class CliOptions(
  config: ScalaUnfmtConfig = ScalaUnfmtConfig.default,
  customFiles: Seq[AbsoluteFile] = Nil,
  customExcludes: Seq[String] = Nil,
  common: CommonOptions = CommonOptions(),
  git: Boolean = false,
  gitOpsConstructor: AbsoluteFile => GitOps = x => new GitOpsImpl(x),
) {
  private[this] val fileFetchMode: FileFetchMode = {
    if (git) GitFiles else RecursiveSearch
  }
  private[this] val gitOps: GitOps = gitOpsConstructor(common.workingDirectory)

  private[this] lazy val filterMatcher: FilterMatcher = {
    val project = ProjectFiles()
    FilterMatcher(
      project.includeFilters.map(OsSpecific.fixSeparatorsInPathPattern),
      (project.excludeFilters ++ customExcludes)
        .map(OsSpecific.fixSeparatorsInPathPattern)
    )
  }

  def getInputMethods: Seq[InputMethod] = {
    val projectFiles: Seq[AbsoluteFile] = getFiles
    projectFiles.map(InputMethod.FileContents.apply)
  }

  def getFiles: Seq[AbsoluteFile] = {
    def canFormat(f: AbsoluteFile) = filterMatcher.matches(f)

    val fetchFiles: AbsoluteFile => Seq[AbsoluteFile] =
      if (fileFetchMode == GitFiles) gitOps.lsTree
      else FileOps.listFiles

    val files: Seq[AbsoluteFile] =
      if (customFiles.isEmpty)
        Seq(common.workingDirectory)
      else
        customFiles

    files.flatMap {
      case d if d.jfile.isDirectory => fetchFiles(d).filter(canFormat)
      case f => Seq(f)
    }
  }
}

case class CommonOptions(
  workingDirectory: AbsoluteFile = AbsoluteFile.userDir
)
