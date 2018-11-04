package com.github.tanishiking.scalaunfmt.config

import metaconfig.{ConfDecoder, generic}
import org.scalafmt.config.BinPack

case class BinPackChoice(
  parentConstructors: List[Boolean],
  literalArgumentLists: List[Boolean],
  literalsMinArgCount: List[Int]
) {
  def combinations: List[BinPack] = {
    for {
      parentConstructorsConf <- parentConstructors
      literalArgumentListsConf <- literalArgumentLists
      literalsMinArgCountConf <- literalsMinArgCount
    } yield {
      BinPack(
        parentConstructors = parentConstructorsConf,
        literalArgumentLists = literalArgumentListsConf,
        literalsMinArgCount = literalsMinArgCountConf
      )
    }
  }
  private implicit val surface: generic.Surface[BinPackChoice] =
    generic.deriveSurface
  val reader: ConfDecoder[BinPackChoice] =
    generic.deriveDecoder(this).noTypos
}

object BinPackChoice {
  val default: BinPackChoice = {
    val binPack = BinPack()
    BinPackChoice(
      parentConstructors   = List(binPack.parentConstructors),
      literalArgumentLists = List(binPack.literalArgumentLists),
      literalsMinArgCount  = List(binPack.literalsMinArgCount)
    )
  }
}
