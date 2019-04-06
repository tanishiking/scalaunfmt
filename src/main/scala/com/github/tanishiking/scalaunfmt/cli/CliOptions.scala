package com.github.tanishiking.scalaunfmt.cli

import java.io.{File, PrintStream}
import java.nio.file.{Path, Paths}

case class CliOptions(
  config: Path = Paths.get(".scalaunfmt.conf"),
  customFiles: Seq[File] = Nil,
  version: String = "",
  output: Option[Path] = None
) {
  def getFiles: Seq[File] = {
    customFiles.flatMap {
      case d if d.isDirectory => listFiles(d)
      case f if f.isFile      => Seq(f)
      case _                  => Seq.empty
    }
  }

  def getPrintStream: PrintStream = {
    output
      .map { o =>
        new PrintStream(o.toFile)
      }
      .getOrElse(
        System.out
      )
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
