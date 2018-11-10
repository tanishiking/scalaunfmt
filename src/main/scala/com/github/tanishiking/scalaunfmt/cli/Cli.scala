package com.github.tanishiking.scalaunfmt.cli

import com.github.tanishiking.scalaunfmt.scalafmt.config.Printer
import info.debatty.java.stringsimilarity.Levenshtein
import info.debatty.java.stringsimilarity.interfaces.MetricStringDistance
import me.tongfei.progressbar.{ProgressBar, ProgressBarBuilder, ProgressBarStyle}
import org.scalafmt.Scalafmt
import org.scalafmt.cli.InputMethod
import org.scalafmt.config.ScalafmtConfig
import org.scalafmt.util.FileOps

object Cli {
  def main(args: Array[String]): Unit = {
    CliArgParser.scoptParser.parse(args, CliOptions()) match {
      case None => sys.exit(1)
      case Some(cliOpt) =>
        val code = run(cliOpt)
        sys.exit(code)
    }
  }

  private[this] def run(cliOpt: CliOptions): Int = {
    val inputMethods = cliOpt.getInputMethods
    val conf = cliOpt.config
    val combinations = conf.combinations
    val codeToConf: Map[Int, ScalafmtConfig] = combinations.map { c =>
      c.hashCode() -> c
    }.toMap

    val levenstein = new Levenshtein()
    val pb = getProgressBar("Formatting...", combinations.length.toLong)

    val codeAndDistance = combinations.map { c =>
      val distance = score(inputMethods, c, levenstein)
      pb.step()
      (c.hashCode(), distance)
    }
    val minCode = codeAndDistance.minBy(_._2)._1
    val result = codeToConf(minCode)

    println("---")
    println(Printer.printHocon(result))
    0
  }

  private[this] def score(inputMethods: Seq[InputMethod], conf: ScalafmtConfig, metric: MetricStringDistance): Double = {
    inputMethods.par.foldLeft(0.0) { (acc, inputMethod) => {
      val sbtConf = conf.copy(runner = conf.runner.forSbt)
      val inputConfig =
        if (inputMethod.isSbt || inputMethod.isSc) sbtConf else conf
      val code = FileOps.readFile(inputMethod.filename)
      val formatted = Scalafmt.format(code, inputConfig, Set.empty, inputMethod.filename).get
      acc + metric.distance(code, formatted)
    }}
  }

  private[this] def getProgressBar(task: String, max: Long): ProgressBar = {
    val pbb = new ProgressBarBuilder().setStyle(ProgressBarStyle.ASCII)
    pbb.setInitialMax(max)
    pbb.setTaskName(task)
    pbb.build()
  }
}
