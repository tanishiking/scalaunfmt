# scalaunfmt
[![Build Status](https://travis-ci.com/tanishiking/scalaunfmt.svg?branch=master)](https://travis-ci.com/tanishiking/scalaunfmt)
[![Latest version](https://index.scala-lang.org/tanishiking/scalaunfmt/scalaunfmt/latest.svg)](https://index.scala-lang.org/tanishiking/scalaunfmt/scalaunfmt)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/f08c70f5d5c64cd882904f2be45256dd)](https://app.codacy.com/app/tanishiking/scalaunfmt?utm_source=github.com&utm_medium=referral&utm_content=tanishiking/scalaunfmt&utm_campaign=Badge_Grade_Dashboard)
[![Scala Steward badge](https://img.shields.io/badge/Scala_Steward-helping-brightgreen.svg?style=flat&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA4AAAAQCAMAAAARSr4IAAAAVFBMVEUAAACHjojlOy5NWlrKzcYRKjGFjIbp293YycuLa3pYY2LSqql4f3pCUFTgSjNodYRmcXUsPD/NTTbjRS+2jomhgnzNc223cGvZS0HaSD0XLjbaSjElhIr+AAAAAXRSTlMAQObYZgAAAHlJREFUCNdNyosOwyAIhWHAQS1Vt7a77/3fcxxdmv0xwmckutAR1nkm4ggbyEcg/wWmlGLDAA3oL50xi6fk5ffZ3E2E3QfZDCcCN2YtbEWZt+Drc6u6rlqv7Uk0LdKqqr5rk2UCRXOk0vmQKGfc94nOJyQjouF9H/wCc9gECEYfONoAAAAASUVORK5CYII=)](https://scala-steward.org)


scalaunfmt generates the `.scalafmt.conf` that have minimum change to existing scala codebase on running scalafmt.
The candidate fields and values for resulting `.scalafmt.conf` can be controlled using configuration for scalaunfmt.

## How to install
```
coursier bootstrap com.github.tanishiking:scalaunfmt_2.12:0.0.5 \
  -r sonatype:releases \
  -o scalaunfmt --standalone --main com.github.tanishiking.scalaunfmt.cli.Cli
```

## Usage
```
Usage: scalaunfmt [options] [<file>...]

  <file>...              file or directory, in which case all *.scala files are formatted.
  -c, --config <value>   a file path to .scalaunfmt.conf (default: .scalaunfmt.conf).
  -v, --version <value>  running version of scalafmt
  -o, --output <value>   output file path (by default, scalaunfmt will write the result to stdout).
```

We can use any versions of scalafmt using `-v` option (scalaunfmt will download the jar for specified version of scalafmt using scalafmt-dynamic).

## Configuration
The configuration for scalaunfmt looks like that for scalafmt, the difference is that the configuration for scalaunfmt requires the list of values on the right-hand side (instead of specific value).
scalaunfmt will generate the multiple configurations for scalafmt using the combinations of rhs of scalaunfmt conf.

For example,

```
maxColumn = [80, 100]
align = [none, some]
```

scalaunfmt will generate the following list of scalafmt conf from the above one, and choose the best one that minimize the change to existing scala codebase.

```
maxColumn = 80
align = none
```

```
maxColumn = 80
align = some
```

```
maxColumn = 100
align = none
```

```
maxColumn = 100
align = more
```

### More examples
For the available list of fields and values for .scalafmt.conf, refer https://scalameta.org/scalafmt/docs/configuration.html

```
continuationIndent.callSite = [2, 4]
continuationIndent.defnSite = [2, 4]
```

```
rewrite.rules = [
  [
    AvoidInfix
    RedundantBraces
  ]
  [
    AvoidInfix
  ]
]
```
