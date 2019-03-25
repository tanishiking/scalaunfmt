package com.github.tanishiking.scalaunfmt.cli

import java.io.File
import java.nio.file.{Path, Paths}

case class CliOptions(
  config: Path = Paths.get(".scalaunfmt.conf"),
  customFiles: Seq[File] = Nil,
  version: String = "",
) {
  def getFiles: Seq[File] = {
    customFiles.flatMap {
      case d if d.isDirectory => listFiles(d)
      case f if f.isFile => Seq(f)
      case _ => Seq.empty
    }
  }

  private[this] def listFiles(file: File): Seq[File] = {
    if (file.isFile) {
      Seq(file)
    } else if (file.isDirectory) {
      file.listFiles.flatMap(listFiles)
    } else {
      Seq.empty
    }
  }
}

