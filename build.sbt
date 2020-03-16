val scalafmtV = "2.3.2"
val metaconfigV = "0.9.4"
val scalatestV = "3.1.0"
val scoptV = "3.7.1"
val similarityV = "1.2.1"

lazy val scalaunfmt = (project in file(".")).
  enablePlugins(BuildInfoPlugin).
  settings(
    name := "scalaunfmt",
    organization := "com.github.tanishiking",
    scalaVersion := "2.12.7",

    libraryDependencies ++= Seq(
      "org.scalameta" %% "scalafmt-dynamic" % scalafmtV,
      "com.geirsson" %% "metaconfig-core" % metaconfigV,
      "com.geirsson" %% "metaconfig-typesafe-config" % metaconfigV,
      "com.github.scopt" %% "scopt" % scoptV,
      "org.scalatest" %% "scalatest" % scalatestV % "test",
      "info.debatty" % "java-string-similarity" % similarityV,
      // undeclared transitive dependency of coursier-small
      "org.scala-lang.modules" %% "scala-xml" % "1.3.0"
    ),

    // Compilation
    scalacOptions ++= Seq(
      "-unchecked",
      "-deprecation",
      "-feature",
      "-opt:l:inline",
      "-opt-inline-from"
    ),

    buildInfoKeys := Seq[BuildInfoKey](version),
    buildInfoPackage := "com.github.tanishiking.scalaunfmt",

    homepage := Some(url("https://github.com/tanishiking/scalaunfmt")),
    licenses := List("MIT License" -> url("http://www.opensource.org/licenses/mit-license.php")),
    developers := List(
      Developer(
        "tanishiking",
        "Rikito Taniguchi",
        "rikiriki1238@gmail.com",
        url("https://github.com/tanishiking")
      )
    )
  )
