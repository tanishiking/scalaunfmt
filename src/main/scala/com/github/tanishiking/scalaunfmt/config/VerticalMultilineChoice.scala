package com.github.tanishiking.scalaunfmt.config

import com.github.tanishiking.scalaunfmt.config
import metaconfig.{ConfDecoder, generic}
import org.scalafmt.config.VerticalMultiline

case class VerticalMultilineChoice (
  atDefnSite: List[Boolean],
  arityThreshold: List[Int],
  newlineBeforeImplicitKW: List[Boolean],
  newlineAfterImplicitKW: List[Boolean],
  newlineAfterOpenParen: List[Boolean]
) {
  def combinations: List[VerticalMultiline] = {
    for {
      atDefnSiteConf <- atDefnSite
      arityThresholdConf <- arityThreshold
      newlineBeforeImplicitKWConf <- newlineBeforeImplicitKW
      newlineAfterImplicitKWConf <- newlineAfterImplicitKW
      newlineAfterOpenParenConf <- newlineAfterOpenParen
    } yield {
      VerticalMultiline(
        atDefnSite = atDefnSiteConf,
        arityThreshold = arityThresholdConf,
        newlineBeforeImplicitKW = newlineBeforeImplicitKWConf,
        newlineAfterImplicitKW = newlineBeforeImplicitKWConf,
        newlineAfterOpenParen = newlineAfterOpenParenConf
      )
    }
  }
  private implicit val surface: generic.Surface[VerticalMultilineChoice] =
    generic.deriveSurface
  val reader: ConfDecoder[VerticalMultilineChoice] =
    generic.deriveDecoder(this).noTypos
}

object VerticalMultilineChoice {
  val default: VerticalMultilineChoice = {
    val verticalMultiline = VerticalMultiline()
    config.VerticalMultilineChoice(
      atDefnSite = List(verticalMultiline.atDefnSite),
      arityThreshold = List(verticalMultiline.arityThreshold),
      newlineBeforeImplicitKW = List(verticalMultiline.newlineBeforeImplicitKW),
      newlineAfterImplicitKW = List(verticalMultiline.newlineAfterImplicitKW),
      newlineAfterOpenParen = List(verticalMultiline.newlineAfterOpenParen)
    )
  }
}
