package com.github.tanishiking.scalaunfmt.cli

import java.io.{ByteArrayOutputStream, PrintStream}
import java.nio.file.{Files, NoSuchFileException, Paths}

import org.scalatest.{FunSpec, Matchers}

class CliSpec extends FunSpec with Matchers {
  describe("Cli") {
    describe("run") {
      it("should show error message to guide users to create a config file") {
        val configPath = Paths.get("NO.SUCH.FILE.conf")
        val opt = CliOptions(
          config = configPath,
          version = "2.0.0-RC5"
        )
        val ex = intercept[IllegalArgumentException] {
          Cli.run(opt, System.out, System.err)
        }
        assert(ex.getMessage.contains("Configuration file NO.SUCH.FILE.conf not found."))
      }

      it("should throw exception for unparsable config") {
        val configPath = Files.createTempFile("temp", ".conf")
        val unparsableConf =
          """
          |align = ["more", "most
          """.stripMargin
        Files.write(configPath, unparsableConf.getBytes)

        val opt = CliOptions(
          config = configPath,
          version = "2.0.0-RC5"
        )
        assertThrows[IllegalArgumentException] {
          Cli.run(opt, System.out, System.err)
        }
      }

      it("should throw exception for invalid config") {
        val configPath = Files.createTempFile("temp", ".conf")
        val invalidConf =
          """
          |align = "more"
          """.stripMargin
        Files.write(configPath, invalidConf.getBytes)

        val opt = CliOptions(
          config = configPath,
          version = "2.0.0-RC5"
        )
        assertThrows[IllegalArgumentException] {
          Cli.run(opt, System.out, System.err)
        }
      }

      it("should write resulting scalafmt.conf to specified PrintStream") {
        val configPath = Files.createTempFile("temp", ".conf")
        val unfmtConf =
          """
          |align = ["more"]
          """.stripMargin
        Files.write(configPath, unfmtConf.getBytes)

        val scalaFile = Files.createTempFile("temp", ".scala")
        val program =
          """
          |object A {}
          """.stripMargin
        Files.write(scalaFile, program.getBytes)

        val opt = CliOptions(
          config = configPath,
          customFiles = Seq(scalaFile.toFile),
          version = "2.0.0-RC5"
        )

        val baos   = new ByteArrayOutputStream()
        val stream = new PrintStream(baos)

        Cli.run(opt, stream, System.err)
        baos.toString.contains("""align = more""") shouldBe true
      }
    }
  }
}
