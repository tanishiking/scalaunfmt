package com.github.tanishiking.scalaunfmt.config

import metaconfig.{ConfDecoder, generic}
import org.scalafmt.config.{SpaceBeforeContextBound, Spaces}

case class SpacesChoice(
  beforeContextBoundColon: List[SpaceBeforeContextBound],
  afterTripleEquals: List[Boolean],
  inImportCurlyBraces: List[Boolean],
  inParentheses: List[Boolean],
  afterKeywordBeforeParen: List[Boolean],
  inByNameTypes: List[Boolean],
  afterSymbolicDefs: List[Boolean]
) {
  def combinations: List[Spaces] = {
    for {
      beforeContextBoundColonConf <- beforeContextBoundColon
      afterTripleEqualsConf <- afterTripleEquals
      inImportCurlyBracesConf <- inImportCurlyBraces
      inParenthesesConf <- inParentheses
      afterKeywordBeforeParenConf <- afterKeywordBeforeParen
      inByNameTypesConf <- inByNameTypes
      afterSymbolicDefsConf <- afterSymbolicDefs
    } yield {
      Spaces(
        beforeContextBoundColon = beforeContextBoundColonConf,
        afterTripleEquals = afterTripleEqualsConf,
        inImportCurlyBraces = inImportCurlyBracesConf,
        inParentheses = inParenthesesConf,
        afterKeywordBeforeParen = afterKeywordBeforeParenConf,
        inByNameTypes = inByNameTypesConf,
        afterSymbolicDefs = afterSymbolicDefsConf
      )
    }
  }
  private implicit val surface: generic.Surface[SpacesChoice] =
    generic.deriveSurface
  val reader: ConfDecoder[SpacesChoice] =
    generic.deriveDecoder(this).noTypos
}

object SpacesChoice {
  val default: SpacesChoice = {
    val spaces = Spaces()
    SpacesChoice(
      beforeContextBoundColon = List(spaces.beforeContextBoundColon),
      afterTripleEquals = List(spaces.afterTripleEquals),
      inImportCurlyBraces = List(spaces.inImportCurlyBraces),
      inParentheses = List(spaces.inParentheses),
      afterKeywordBeforeParen = List(spaces.afterKeywordBeforeParen),
      inByNameTypes = List(spaces.inByNameTypes),
      afterSymbolicDefs = List(spaces.afterSymbolicDefs)
    )
  }
}
