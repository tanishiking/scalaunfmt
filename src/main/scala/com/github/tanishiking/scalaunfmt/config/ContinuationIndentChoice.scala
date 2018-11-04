package com.github.tanishiking.scalaunfmt.config

import metaconfig.{ConfDecoder, generic}
import org.scalafmt.config.ContinuationIndent

case class ContinuationIndentChoice(
  callSite: List[Int],
  defnSite: List[Int],
  extendSite: List[Int]
) {
  def combinations: List[ContinuationIndent] = {
    for {
      callSiteConf <- callSite
      defnSiteConf <- defnSite
      extendSiteConf <- extendSite
    } yield {
      ContinuationIndent(
        callSite = callSiteConf,
        defnSite = defnSiteConf,
        extendSite = extendSiteConf
      )
    }
  }
  private implicit val surface: generic.Surface[ContinuationIndentChoice] =
    generic.deriveSurface
  val reader: ConfDecoder[ContinuationIndentChoice] =
    generic.deriveDecoder(this).noTypos
}

object ContinuationIndentChoice {
  val default: ContinuationIndentChoice = {
    val continuationIndent = ContinuationIndent()
    ContinuationIndentChoice(
      callSite = List(continuationIndent.callSite),
      defnSite = List(continuationIndent.defnSite),
      extendSite = List(continuationIndent.extendSite)
    )
  }
}
