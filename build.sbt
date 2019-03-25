val scalafmtV = "2.0.0-RC5"
val metaconfigV = "0.8.3"
val scalatestV = "3.0.5"
val scoptV = "3.7.0"
val similarityV = "1.0.1"

lazy val scalaunfmt = (project in file(".")).
  settings(
    name := "scalaunfmt",
    organization := "com.github.tanishiking",
    version := "0.0.2",
    scalaVersion := "2.12.7",

    libraryDependencies ++= Seq(
      "org.scalameta" %% "scalafmt-dynamic" % scalafmtV,
      "com.geirsson" %% "metaconfig-core" % metaconfigV,
      "com.geirsson" %% "metaconfig-typesafe-config" % metaconfigV,
      "com.github.scopt" %% "scopt" % scoptV,
      "org.scalatest" %% "scalatest" % scalatestV % "test",
      "info.debatty" % "java-string-similarity" % similarityV,
      // undeclared transitive dependency of coursier-small
      "org.scala-lang.modules" %% "scala-xml" % "1.1.1"
    ),

    // Compilation
    scalacOptions ++= Seq(
      "-unchecked",
      "-deprecation",
      "-feature",
      "-opt:l:inline",
      "-opt-inline-from"
    ),

    // Publishing
    publishMavenStyle := true,
    publishTo := Some(
      if (isSnapshot.value)
        Opts.resolver.sonatypeSnapshots
      else
        Opts.resolver.sonatypeStaging
    ),
    publishArtifact in Test := false,
    pomIncludeRepository := { _ => false },
    pomExtra := <url>https://github.com/tanishiking/scalaunfmt</url>
      <licenses>
        <license>
          <name>MIT License</name>
          <url>http://www.opensource.org/licenses/mit-license.php</url>
          <distribution>repo</distribution>
        </license>
      </licenses>
      <scm>
        <url>git@github.com:tanishiking/scalaunfmt.git</url>
        <connection>scm:git:git@github.com:tanishiking/scalaunfmt.git</connection>
      </scm>
      <developers>
        <developer>
          <id>tanishiking</id>
          <name>Rikito Taniguchi</name>
          <url>https://github.com/tanishiking</url>
        </developer>
      </developers>
  )
