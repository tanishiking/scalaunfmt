package com.github.tanishiking.scalaunfmt.cli

import java.io.File

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
      opt[String]('c', "config")
        .required()
        .action((path, c) => {
          val absFile = new File(path).getAbsoluteFile
          c.copy(config = absFile.toPath)
        })
        .text("a file path to .scalaunfmt.conf")
      opt[String]('v', "version")
        .required()
        .action((version, c) => {
          c.copy(version = version)
        })
        .text("running version of scalafmt")
    }

  private[this] def addFile(file: File, c: CliOptions): CliOptions = {
    c.copy(customFiles = c.customFiles :+ file.getAbsoluteFile)
  }
}
