# scalaunfmt
[![Build Status](https://travis-ci.com/tanishiking/scalaunfmt.svg?branch=master)](https://travis-ci.com/tanishiking/scalaunfmt)

scalaunfmt generates the `.scalafmt.conf` that have minimum change to existing scala codebase on running scalafmt.
The candidate fields and values for resulting `.scalafmt.conf` can be controlled using configuration for scalaunfmt.

## How to install
```
coursier bootstrap com.github.tanishiking:scalaunfmt_2.12:0.0.3 \
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
