package com.github.tanishiking.scalaunfmt.config

import metaconfig.generic.Surface
import metaconfig.{Conf, ConfCodec, ConfDecoder, ConfError, Configured, Input, MetaconfigParser, generic}
import org.scalafmt.config.{Align, Docstrings, LineEndings, ScalafmtConfig, ScalafmtRunner}
import org.scalafmt.util.{AbsoluteFile, FileOps}

case class ScalaUnfmtConfig(
  maxColumn: List[Int],
  align: List[Align],
  docstrings: List[Docstrings],
  optIn: OptInChoice,
  binPack: BinPackChoice,
  continuationIndent: ContinuationIndentChoice,
  spaces: SpacesChoice,
  literals: LiteralsChoice,
  lineEndings: List[LineEndings],
  newlines: NewlinesChoice,
  indentYieldKeyword: List[Boolean],
  unindentTopLevelOperators: List[Boolean],
  includeCurlyBraceInSelectChains: List[Boolean],
  assumeStandardLibraryStripMargin: List[Boolean],
  danglingParentheses: List[Boolean],
  poorMansTrailingCommasInConfigStyle: List[Boolean],
  verticalMultiline: VerticalMultilineChoice,
  runner: ScalafmtRunner = ScalafmtRunner()
) {
  private implicit val alignReader: ConfDecoder[Align] = ConfDecoder.instanceF[Align] {
    case Conf.Str("none") => Configured.ok(Align.none)
    case Conf.Str("some") => Configured.ok(Align.some)
    case Conf.Str("more") => Configured.ok(Align.more)
    case Conf.Str("most") => Configured.ok(Align.most)
    case _ => Configured.notOk(ConfError.message("align must be one of none|some|more|most"))
  }
  private implicit val docstringsReader: ConfDecoder[Docstrings] = Docstrings.reader
  private implicit val optInChoiceReader: ConfDecoder[OptInChoice] = optIn.reader
  private implicit val binPackChoiceReader: ConfDecoder[BinPackChoice] = binPack.reader
  private implicit val continuationIndentChoiceReader: ConfDecoder[ContinuationIndentChoice] = continuationIndent.reader
  private implicit val spacesChoiceReader: ConfDecoder[SpacesChoice] = spaces.reader
  private implicit val literalsChoiceReader: ConfDecoder[LiteralsChoice] = literals.reader
  private implicit val lineEndingsReader: ConfCodec[LineEndings] = LineEndings.reader
  private implicit val newlinesChoiceReader: ConfDecoder[NewlinesChoice] = newlines.reader
  private implicit val verticalMultilineChoiceReader: ConfDecoder[VerticalMultilineChoice] = verticalMultiline.reader
  private implicit val runnerReader: ConfDecoder[ScalafmtRunner] = runner.reader

  private val reader: ConfDecoder[ScalaUnfmtConfig] = generic.deriveDecoder(this).noTypos.noTypos

  def combinations: List[ScalafmtConfig] = {
    for {
      alignConf <- align
      maxColumnConf <- maxColumn
      docstringsConf <- docstrings
      continuationIndentConf <- continuationIndent.combinations
      binPackConf <- binPack.combinations
      spacesConf <- spaces.combinations
      literalsConf <- literals.combinations
      lineEndingsConf <- lineEndings
      newlinesConf <- newlines.combinations
      indentYieldKeywordConf <- indentYieldKeyword
      unindentTopLevelOperatorsConf <- unindentTopLevelOperators
      includeCurlyBraceInSelectChainsConf <- includeCurlyBraceInSelectChains
      assumeStandardLibraryStripMarginConf <- assumeStandardLibraryStripMargin
      danglingParenthesesConf <- danglingParentheses
      poorMansTrailingCommasInConfigStyleConf <- poorMansTrailingCommasInConfigStyle
      verticalMultilineConf <- verticalMultiline.combinations
    } yield {
      ScalafmtConfig(
        align = alignConf,
        maxColumn = maxColumnConf,
        docstrings = docstringsConf,
        continuationIndent = continuationIndentConf,
        binPack = binPackConf,
        spaces = spacesConf,
        literals = literalsConf,
        lineEndings = lineEndingsConf,
        newlines = newlinesConf,
        indentYieldKeyword = indentYieldKeywordConf,
        unindentTopLevelOperators = unindentTopLevelOperatorsConf,
        includeCurlyBraceInSelectChains = includeCurlyBraceInSelectChainsConf,
        assumeStandardLibraryStripMargin = assumeStandardLibraryStripMarginConf,
        danglingParentheses = danglingParenthesesConf,
        poorMansTrailingCommasInConfigStyle = poorMansTrailingCommasInConfigStyleConf,
        verticalMultiline = verticalMultilineConf
      )
    }
  }
}

object ScalaUnfmtConfig {
  def configReader: ConfDecoder[ScalaUnfmtConfig] = ConfDecoder.instance[ScalaUnfmtConfig] {
    case conf @ Conf.Obj(_) => default.reader.read(conf)
  }

  private implicit val surface: Surface[ScalaUnfmtConfig] = generic.deriveSurface[ScalaUnfmtConfig]

  val default = ScalaUnfmtConfig(
    align = List(Align.default),
    maxColumn = List(ScalafmtConfig.default.maxColumn),
    docstrings = List(ScalafmtConfig.default.docstrings),
    optIn = OptInChoice.default,
    binPack = BinPackChoice.default,
    continuationIndent = ContinuationIndentChoice.default,
    spaces = SpacesChoice.default,
    literals = LiteralsChoice.default,
    lineEndings = List(ScalafmtConfig.default.lineEndings),
    newlines = NewlinesChoice.default,
    indentYieldKeyword = List(ScalafmtConfig.default.indentYieldKeyword),
    unindentTopLevelOperators = List(ScalafmtConfig.default.unindentTopLevelOperators),
    includeCurlyBraceInSelectChains = List(ScalafmtConfig.default.includeCurlyBraceInSelectChains),
    assumeStandardLibraryStripMargin = List(ScalafmtConfig.default.assumeStandardLibraryStripMargin),
    danglingParentheses = List(ScalafmtConfig.default.danglingParentheses),
    poorMansTrailingCommasInConfigStyle = List(ScalafmtConfig.default.poorMansTrailingCommasInConfigStyle),
    verticalMultiline = VerticalMultilineChoice.default
  )

  def fromHoconString(
    content: String
  ): Configured[ScalaUnfmtConfig] = {
    import metaconfig.typesafeconfig.typesafeConfigMetaconfigParser
    val conf: Configured[Conf] = implicitly[MetaconfigParser].fromInput(Input.String(content))
    configReader.read(conf)
  }

  def fromHoconFile(
    file: java.io.File,
    workingDirectory: AbsoluteFile
  ): Configured[ScalaUnfmtConfig] = {
    if (!file.exists()) Configured.notOk(ConfError.fileDoesNotExist(file))
    else {
      val absFile = AbsoluteFile.fromFile(file, workingDirectory)
      val content = FileOps.readFile(absFile)
      ScalaUnfmtConfig.fromHoconString(content)
    }
  }
}

