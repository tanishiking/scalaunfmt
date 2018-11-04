package com.github.tanishiking.scalaunfmt
package config

import java.io.File

import metaconfig.{Conf, ConfError, Configured}
import org.scalafmt.config.{Align, ScalafmtConfig}
import org.scalafmt.config.Docstrings.{JavaDoc, ScalaDoc}
import org.scalafmt.util.AbsoluteFile
import org.scalatest.{FunSpec, Matchers}

class ScalaUnfmtConfigSpec extends FunSpec with Matchers {
  private val testDir = AbsoluteFile.fromPath(
    new File("src/test/resources/scalaunfmtconfig".replace("/", File.separator)).getAbsolutePath
  ).get

  describe("ScalaUnfmtConfig") {
    describe("fromHoconString") {
      it("should construct scalaunfmt config") {
        val hocon =
          """
          | align = [more, most]
          | maxColumn = [80, 100, 120]
          | docstrings = [JavaDoc, ScalaDoc]
          | optIn.breakChainOnFirstMethodDot = [true, false]
          | optIn.blankLineBeforeDocstring = [true, false]
          | binPack.literalsMinArgCount = [50, 100]
          | continuationIndent.callSite = [2, 4]
          | continuationIndent.defnSite = [2, 4]
          """.stripMargin
        val expectedOptIn = OptInChoice.default
          .copy(breakChainOnFirstMethodDot = List(true, false))
          .copy(blankLineBeforeDocstring = List(true, false))
        val expectedBinPack = BinPackChoice.default
          .copy(literalsMinArgCount = List(50, 100))
        val expectedContinuationIndent = ContinuationIndentChoice.default
          .copy(callSite = List(2, 4))
          .copy(defnSite = List(2, 4))
        val expected = ScalaUnfmtConfig.default
            .copy(align = List(Align.more, Align.most))
            .copy(maxColumn = List(80, 100, 120))
            .copy(docstrings = List(JavaDoc, ScalaDoc))
            .copy(optIn = expectedOptIn)
            .copy(binPack = expectedBinPack)
            .copy(continuationIndent = expectedContinuationIndent)
        ScalaUnfmtConfig.fromHoconString(hocon).get should equal (expected)
      }

      it("should fail to read invalid configuration") {
        val typeMismatch =
          """
          | maxColumn = [80, 100, foo]
          """.stripMargin
        ScalaUnfmtConfig.fromHoconString(typeMismatch).isNotOk should equal(true)

        val invalidField =
          """
          | aaaaaaa = [foo, bar]
          """.stripMargin
        ScalaUnfmtConfig.fromHoconString(invalidField).isNotOk should equal(true)

        val invalidValue =
          """
          | docstrings = [InvalidDoc, JavaDoc]
          """.stripMargin
        ScalaUnfmtConfig.fromHoconString(invalidValue).isNotOk should equal(true)

        val notList =
          """
          | align = more
          """.stripMargin
        ScalaUnfmtConfig.fromHoconString(notList).isNotOk should equal(true)
      }
    }

    describe("fromHoconFile") {
      it("should fail to read if file doesn't exist") {
        val file = new File("notexist.conf")
        ScalaUnfmtConfig.fromHoconFile(file, testDir) should equal(
          Configured.notOk(ConfError.fileDoesNotExist(file))
        )
      }

      it("should read option from file") {
        val expected = ScalaUnfmtConfig.default
            .copy(align = List(Align.some, Align.more))
        ScalaUnfmtConfig.fromHoconFile(new File("align.conf"), testDir).get should equal(expected)
      }
    }

    describe("combinations") {
      it("should generate ScalafmtConfig.default from ScalaUnfmtConfig.default") {
        val config = ScalaUnfmtConfig.default.combinations.head
        val revised = org.scalafmt.config.ScalafmtConfig.encoder.write(config)
        val default = org.scalafmt.config.ScalafmtConfig.encoder.write(ScalafmtConfig.default)
        val diff = Conf.patch(default, revised)
        Conf.printHocon(diff) should equal("")
      }

      it("should generate multiple configurations") {
        val unfmtConfig = ScalaUnfmtConfig.default
          .copy(align = List(Align.some, Align.more))
          .copy(docstrings = List(JavaDoc, ScalaDoc))
          .copy(continuationIndent =
              ContinuationIndentChoice.default
                .copy(defnSite = List(2, 4))
                .copy(callSite = List(2, 4, 8))
          )
        unfmtConfig.combinations.length should equal(2*2*2*3)
        unfmtConfig.combinations.toSet.toSeq.length should equal(2*2*2*3)
      }
    }
  }
}
