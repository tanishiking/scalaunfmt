# scalaunfmt
Scalaunfmt generates `.scalafmt.conf` file from example codebase and configuration candidates.

## Install
```
coursier bootstrap com.github.tanishiking:scalaunfmt_2.12:0.0.1 \
  -r sonatype:releases \
  -o scalaunfmt --standalone --main com.github.tanishiking.scalaunfmt.cli.Cli
```
