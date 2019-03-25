package com.github.tanishiking.scalaunfmt.cli

import java.nio.file.Paths

import org.scalatest.{FunSpec, Matchers}

class CliArgParserSpec extends FunSpec with Matchers {
  describe("CliArgParser") {
    describe("scoptParser") {
      it("should return None if required field was not specified") {
        val parsed = CliArgParser.scoptParser.parse(Array("-v", "2.0.0-RC5", "dummy.scala"), CliOptions())
        parsed.isEmpty shouldBe true
      }

      it("can add multiple files") {
        val parsed = CliArgParser.scoptParser.parse(
          Array("-v", "2.0.0-RC5", "-c", "/dummy.conf", "dummy1.scala", "dummy2.scala"),
          CliOptions()
        )
        val opt = parsed.get
        opt.version shouldBe "2.0.0-RC5"
        opt.config shouldBe Paths.get("/dummy.conf")
        opt.customFiles should contain theSameElementsAs Seq(
          Paths.get("dummy1.scala").toFile.getAbsoluteFile,
          Paths.get("dummy2.scala").toFile.getAbsoluteFile
        )
      }
    }
  }
}
