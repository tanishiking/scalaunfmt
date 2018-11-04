package com.github.tanishiking.scalaunfmt.config

import metaconfig.{ConfDecoder, generic}
import org.scalafmt.config.{Case, Literals}

case class LiteralsChoice(
  long: List[Case],
  float: List[Case],
  double: List[Case]
) {
  def combinations: List[Literals] = {
    for {
      longConf <- long
      floatConf <- float
      doubleConf <- double
    } yield {
      Literals(
        long = longConf,
        float = floatConf,
        double = doubleConf
      )
    }
  }
  private implicit val surface: generic.Surface[LiteralsChoice] =
    generic.deriveSurface
  val reader: ConfDecoder[LiteralsChoice] =
    generic.deriveDecoder(this).noTypos
}

object LiteralsChoice {
  val default: LiteralsChoice = {
    val literals = Literals()
    LiteralsChoice(
      long = List(literals.long),
      float = List(literals.float),
      double = List(literals.double)
    )
  }
}
