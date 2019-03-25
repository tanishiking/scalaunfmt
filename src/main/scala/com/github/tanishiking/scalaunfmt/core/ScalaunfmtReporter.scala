package com.github.tanishiking.scalaunfmt.core

import java.io.{PrintStream, PrintWriter}
import java.nio.file.Path
import java.util.concurrent.atomic.AtomicReference
import java.util.function.UnaryOperator

import org.scalafmt.interfaces.ScalafmtReporter

class ScalaunfmtReporter(out: PrintStream) extends ScalafmtReporter {
  private val messages = new AtomicReference[Seq[String]](Seq.empty)

  def error: Option[IllegalArgumentException] =
    if (messages.get.isEmpty) None
    else {
      val err = new IllegalArgumentException(messages.get.mkString("\n"))
      Some(err)
    }

  override def error(file: Path, message: String): Unit = {
    messages.updateAndGet(new UnaryOperator[Seq[String]] {
      override def apply(t: Seq[String]): Seq[String] = t :+ message
    })
  }

  override def error(file: Path, e: Throwable): Unit = {
    error(file, e.getMessage)
  }

  override def excluded(file: Path): Unit =
    out.println(s"file excluded: $file")

  override def parsedConfig(config: Path, scalafmtVersion: String): Unit = {}

  override def downloadWriter(): PrintWriter = new PrintWriter(out)
}
