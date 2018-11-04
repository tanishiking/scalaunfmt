package com.github.tanishiking.scalaunfmt.cli

import java.io.File

import com.github.tanishiking.scalaunfmt.config.ScalaUnfmtConfig
import org.scalafmt.util.AbsoluteFile
import scopt.OptionParser


object CliArgParser {
  val scoptParser: OptionParser[CliOptions] =
    new scopt.OptionParser[CliOptions]("scalaunfmt") {

      head("scalaunfmt", "0.0.1")
      arg[File]("<file>...")
        .optional()
        .unbounded()
        .action((file, c) => addFile(file, c))
        .text("file or directory, in which case all *.scala files are formatted.")
      opt[Seq[String]]("exclude")
        .action((excludes, c) => c.copy(customExcludes = excludes))
        .text("file or directory, in which case all *.scala files are formatted.")
      opt[Unit]("git")
          .action((_, c) => c.copy(git = true))
          .text("if true, ignore files in .gitignore (default false)")
      opt[String]('c', "config")
        .required()
        .action((path, c) => {
          val conf = ScalaUnfmtConfig.fromHoconFile(new File(path), c.common.workingDirectory)
          c.copy(config = conf.get)
        })
        .text("a file path to .scalaunfmt.conf")
    }

  private[this] def addFile(file: File, c: CliOptions): CliOptions = {
    val absFile = AbsoluteFile.fromFile(file, c.common.workingDirectory)
    c.copy(customFiles = c.customFiles :+ absFile)
  }
}
