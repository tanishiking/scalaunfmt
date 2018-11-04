package com.github.tanishiking.scalaunfmt
package scalafmt.config

import metaconfig.{Conf, ConfEncoder}
import org.scalafmt.config.{Align, ScalafmtConfig}

object Printer {
  def printHocon(config: ScalafmtConfig): String = {
    val revised = org.scalafmt.config.ScalafmtConfig.encoder.write(config)
    val default = org.scalafmt.config.ScalafmtConfig.encoder.write(ScalafmtConfig.default)
    val diff = Conf.patch(default, revised)
    Conf.printHocon(transform(diff))
  }

  private[this] def transform(original: Conf): Conf = {
    transformRec(original)
  }

  private[this] def transformRec(c: Conf): Conf = {
    c match {
      case str @ Conf.Str(_) => str
      case num @ Conf.Num(_) => num
      case bool @ Conf.Bool(_) => bool
      case n @ Conf.Null() => n
      case Conf.Lst(lst) => Conf.fromList(lst.map(transformRec))
      case Conf.Obj(obj) =>
        val m = obj.map { case (k, v) =>
          if (k == "align") {
            val value = v match {
              case `none` => Conf.fromString("none")
              case `some` => Conf.fromString("some")
              case `more` => Conf.fromString("more")
              case `most` => Conf.fromString("most")
              case other  => other
            }
            (k, value)
          } else {
            (k, transformRec(v))
          }
        }.toMap
        Conf.fromMap(m)
    }
  }
  private[this] val none = Conf.patch(ConfEncoder[Align].write(Align()), ConfEncoder[Align].write(Align.none))
  private[this] val some = Conf.patch(ConfEncoder[Align].write(Align()), ConfEncoder[Align].write(Align.some))
  private[this] val more = Conf.patch(ConfEncoder[Align].write(Align()), ConfEncoder[Align].write(Align.more))
  private[this] val most = Conf.patch(ConfEncoder[Align].write(Align()), ConfEncoder[Align].write(Align.most))
}
