# scalaunfmt
[![Build Status](https://travis-ci.com/tanishiking/scalaunfmt.svg?branch=master)](https://travis-ci.com/tanishiking/scalaunfmt)

scalaunfmt determines the most fit `.scalafmt.conf` with specified scala source codes.
The `.scalafmt.conf` will be chosen from scalaunfmt configuration.

**Currently, scalaunfmt supports only scalafmt 1.6.0-RC4**

## How to install
```
coursier bootstrap com.github.tanishiking:scalaunfmt_2.12:0.0.1 \
  -r sonatype:releases \
  -o scalaunfmt --standalone --main com.github.tanishiking.scalaunfmt.cli.Cli
```

## Usage
Configuration for scalaunfmt that describes configuration candidates for `.scalafmt.conf`, is defined in a plain text file using HOCON syntax.

Here is an example:

```
maxColumn = [80, 100]
align = [none, some, more, most]
docstrings = [JavaDoc, ScalaDoc]
verticalMultiline.newlineAfterImplicitKW = [true, false]
```

This scalaunfmt configuration means that scalaunfmt should choose `.scalafmt.conf` of which

- `maxColumn` is either `80` or `100` and
- `align` is `none` or `some` or `more` or `most` and
- `docstrings` is `JavaDoc` or `ScalaDoc` and
- `verticalMultiline.newlineAfterImplicitKW` is `true` or `false`

```
Usage: scalaunfmt [options] [<file>...]

  <file>...             file or directory, in which case all *.scala files are formatted.
  --exclude <value>     file or directory, in which case all *.scala files are formatted.
  --git                 if true, ignore files in .gitignore (default false)
  -c, --config <value>  a file path to .scalaunfmt.conf
```

## Configuration
Here is a list of configurable scalafmt options using `conf = [foo, bar]` notation, like `maxColumn = [80, 100, 120]`

- `maxColumn List[Int]`
- `align: List[Align]` (`Align` is `enum(none, some, more, most)`)
- `docstrings: List[Docstrings]` (`Docstrings` is `enum(JavaDoc, ScalaDoc, preserve)`)
- `optIn.configStyleArguments: List[Boolean]`
- `optIn.breaksInsideChains: List[Boolean]`
- `optIn.breakChainOnFirstMethodDot: List[Boolean]`
- `optIn.selfAnnotationNewline: List[Boolean]`
- `optIn.annotationNewlines: List[Boolean]`
- `optIn.blankLineBeforeDocstring: List[Boolean]`
- `binPack.parentConstructors: List[Boolean]`
- `binPack.literalArgumentLists: List[Boolean]`
- `binPack.literalsMinArgCount: List[Int]`
- `continuationIndent.callSite: List[Int]`
- `continuationIndent.defnSite: List[Int]`
- `continuationIndent.extendSite: List[Int]`
- `spaces.beforeContextBoundColon: List[SpaceBeforeContextBound]` (`SpaceBeforeContextBound` is `enum(Always, Never, IfMultipleBounds)`)
- `spaces.afterTripleEquals: List[Boolean]`
- `spaces.inImportCurlyBraces: List[Boolean]`
- `spaces.inParentheses: List[Boolean]`
- `spaces.afterKeywordBeforeParen: List[Boolean]`
- `spaces.inByNameTypes: List[Boolean]`
- `spaces.afterSymbolicDefs: List[Boolean]`
- `literals.long: List[Case]` (`Case` is `enum(Upper, Lower, Unchanged)`)
- `literals.float: List[Case]`
- `literals.double: List[Case]`
- `lineEndings: List[LineEndings]` (`LineEndings` is `enum(unix, windows, preserve)`)
- `newlines.neverInResultType: List[Boolean]`
- `newlines.neverBeforeJsNative: List[Boolean]`
- `newlines.sometimesBeforeColonInMethodReturnType: List[Boolean]`
- `newlines.penalizeSingleSelectMultiArgList: List[Boolean]`
- `newlines.alwaysBeforeCurlyBraceLambdaParams: List[Boolean]`
- `newlines.alwaysBeforeTopLevelStatements: List[Boolean]`
- `newlines.afterCurlyLambda: List[NewlineCurlyLambda]` (`NewlineCurlyLambda` is `enum(preserve, always, never)`)
- `newlines.alwaysBeforeElseAfterCurlyIf: List[Boolean]`
- `newlines.alwaysBeforeMultilineDef: List[Boolean]`
- `indentYieldKeyword: List[Boolean]`
- `unindentTopLevelOperators: List[Boolean]`
- `includeCurlyBraceInSelectChains: List[Boolean]`
- `assumeStandardLibraryStripMargin: List[Boolean]`
- `danglingParentheses: List[Boolean]`
- `poorMansTrailingCommasInConfigStyle: List[Boolean]`
- `verticalMultiline.atDefnSite: List[Boolean]`
- `verticalMultiline.arityThreshold: List[Int]`
- `verticalMultiline.newlineBeforeImplicitKW: List[Boolean]`
- `verticalMultiline.newlineAfterImplicitKW: List[Boolean]`
- `verticalMultiline.newlineAfterOpenParen: List[Boolean]`
