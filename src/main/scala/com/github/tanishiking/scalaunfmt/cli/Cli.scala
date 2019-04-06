package com.github.tanishiking.scalaunfmt.cli

import java.io.PrintStream

import com.github.tanishiking.scalaunfmt.core.Runner
import metaconfig.{Conf, Configured}
import metaconfig.typesafeconfig._

object Cli {
  def main(args: Array[String]): Unit = {
    CliArgParser.scoptParser.parse(args, CliOptions()) match {
      case None => sys.exit(1)
      case Some(cliOpt) =>
        val code = run(cliOpt, cliOpt.getPrintStream, System.err)
        sys.exit(code)
    }
  }

  private[cli] def run(cliOpt: CliOptions, outStream: PrintStream, errStream: PrintStream): Int = {
    val files    = cliOpt.getFiles
    val confPath = cliOpt.config

    val runner = new Runner(errStream)

    Conf.parseFile(confPath.toFile) match {
      case Configured.Ok(value) =>
        runner.run(
          files,
          cliOpt.version,
          value
        ) match {
          case Right(confString) => outStream.println(confString)
          case Left(throwable)   => throw throwable
        }
      case Configured.NotOk(err) =>
        throw new IllegalArgumentException(err.msg)
    }
    0
  }
}
