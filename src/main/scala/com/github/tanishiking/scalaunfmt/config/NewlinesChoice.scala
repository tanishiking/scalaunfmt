package com.github.tanishiking.scalaunfmt.config

import metaconfig.{ConfDecoder, generic}
import org.scalafmt.config.{NewlineCurlyLambda, Newlines}

case class NewlinesChoice(
  neverInResultType: List[Boolean],
  neverBeforeJsNative: List[Boolean],
  sometimesBeforeColonInMethodReturnType: List[Boolean],
  penalizeSingleSelectMultiArgList: List[Boolean],
  alwaysBeforeCurlyBraceLambdaParams: List[Boolean],
  alwaysBeforeTopLevelStatements: List[Boolean],
  afterCurlyLambda: List[NewlineCurlyLambda],
  alwaysBeforeElseAfterCurlyIf: List[Boolean],
  alwaysBeforeMultilineDef: List[Boolean]
) {
  def combinations: List[Newlines] = {
    for {
      neverInResultTypeConf <- neverInResultType
      neverBeforeJsNativeConf <- neverBeforeJsNative
      sometimesBeforeColonInMethodReturnTypeConf <- sometimesBeforeColonInMethodReturnType
      penalizeSingleSelectMultiArgListConf <- penalizeSingleSelectMultiArgList
      alwaysBeforeCurlyBraceLambdaParamsConf <- alwaysBeforeCurlyBraceLambdaParams
      alwaysBeforeTopLevelStatementsConf <- alwaysBeforeTopLevelStatements
      afterCurlyLambdaConf <- afterCurlyLambda
      alwaysBeforeElseAfterCurlyIfConf <- alwaysBeforeElseAfterCurlyIf
      alwaysBeforeMultilineDefConf <- alwaysBeforeMultilineDef
    } yield {
      Newlines(
        neverInResultType = neverInResultTypeConf,
        neverBeforeJsNative = neverBeforeJsNativeConf,
        sometimesBeforeColonInMethodReturnType = sometimesBeforeColonInMethodReturnTypeConf,
        penalizeSingleSelectMultiArgList = penalizeSingleSelectMultiArgListConf,
        alwaysBeforeCurlyBraceLambdaParams = alwaysBeforeCurlyBraceLambdaParamsConf,
        alwaysBeforeTopLevelStatements = alwaysBeforeTopLevelStatementsConf,
        afterCurlyLambda = afterCurlyLambdaConf,
        alwaysBeforeElseAfterCurlyIf = alwaysBeforeElseAfterCurlyIfConf,
        alwaysBeforeMultilineDef = alwaysBeforeMultilineDefConf
      )
    }
  }
  private implicit val surface: generic.Surface[NewlinesChoice] =
    generic.deriveSurface
  val reader: ConfDecoder[NewlinesChoice] =
    generic.deriveDecoder(this).noTypos
}

object NewlinesChoice {
  val default: NewlinesChoice = {
    val newlines = Newlines()
    NewlinesChoice(
      neverInResultType = List(newlines.neverInResultType),
      neverBeforeJsNative = List(newlines.neverBeforeJsNative),
      sometimesBeforeColonInMethodReturnType = List(newlines.sometimesBeforeColonInMethodReturnType),
      penalizeSingleSelectMultiArgList = List(newlines.penalizeSingleSelectMultiArgList),
      alwaysBeforeCurlyBraceLambdaParams = List(newlines.alwaysBeforeCurlyBraceLambdaParams),
      alwaysBeforeTopLevelStatements = List(newlines.alwaysBeforeTopLevelStatements),
      afterCurlyLambda = List(newlines.afterCurlyLambda),
      alwaysBeforeElseAfterCurlyIf = List(newlines.alwaysBeforeElseAfterCurlyIf),
      alwaysBeforeMultilineDef = List(newlines.alwaysBeforeMultilineDef)
    )
  }
}