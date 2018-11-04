package com.github.tanishiking.scalaunfmt.config

import metaconfig.{ConfDecoder, generic}
import org.scalafmt.config.OptIn

case class OptInChoice(
  configStyleArguments: List[Boolean],
  breaksInsideChains: List[Boolean],
  breakChainOnFirstMethodDot: List[Boolean],
  selfAnnotationNewline: List[Boolean],
  annotationNewlines: List[Boolean],
  blankLineBeforeDocstring: List[Boolean]
) {
  def combinations: List[OptIn] = {
    for {
      configStyleArgumentsConf       <- configStyleArguments
      breaksInsideChainsConf         <- breaksInsideChains
      breakChainOnFirstMethodDotConf <- breakChainOnFirstMethodDot
      selfAnnotationNewlineConf      <- selfAnnotationNewline
      annotationNewlinesConf         <- annotationNewlines
      blankLineBeforeDocstringConf   <- blankLineBeforeDocstring
    } yield {
      OptIn(
        configStyleArguments       = configStyleArgumentsConf,
        breaksInsideChains         = breaksInsideChainsConf,
        breakChainOnFirstMethodDot = breakChainOnFirstMethodDotConf,
        selfAnnotationNewline      = selfAnnotationNewlineConf,
        annotationNewlines         = annotationNewlinesConf,
        blankLineBeforeDocstring   = blankLineBeforeDocstringConf
      )
    }
  }
  private implicit val surface: generic.Surface[OptInChoice] =
    generic.deriveSurface
  val reader: ConfDecoder[OptInChoice] =
    generic.deriveDecoder(this).noTypos
}

object OptInChoice {
  val default: OptInChoice = {
    val optIn = OptIn()
    OptInChoice(
      configStyleArguments = List(optIn.configStyleArguments),
      breaksInsideChains = List(optIn.breaksInsideChains),
      breakChainOnFirstMethodDot = List(optIn.breakChainOnFirstMethodDot),
      selfAnnotationNewline = List(optIn.selfAnnotationNewline),
      annotationNewlines = List(optIn.annotationNewlines),
      blankLineBeforeDocstring = List(optIn.blankLineBeforeDocstring)
    )
  }
}
