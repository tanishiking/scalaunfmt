package com.github.tanishiking.scalaunfmt.cli

import java.nio.file.{Files, Paths}

import org.scalatest.{FunSpec, Matchers}

class CliOptionsSpec extends FunSpec with Matchers {
  describe("CliOptions") {
    describe("getFiles") {
      it("should return empty for non-exist files") {
        val path = Paths.get("/non/exist/file")
        val opt = CliOptions(
          version = "2.0.0-RC5",
          customFiles = Seq(path.toFile)
        )
        opt.getFiles shouldBe Seq.empty
      }

      it("should return files specified by customFiles if they are files") {
        val path1 = Files.createTempFile("dummy", ".conf")
        val path2 = Files.createTempFile("dummy", ".conf")
        val opt = CliOptions(
          version = "2.0.0-RC5",
          customFiles = Seq(path1.toFile, path2.toFile)
        )
        opt.getFiles should contain theSameElementsAs Seq(path1.toFile, path2.toFile)
      }

      it("should return files in the specified directory") {
        val dir   = Files.createTempDirectory("dir")
        val temp1 = Files.createTempFile(dir, "dummy", ".conf")
        val temp2 = Files.createTempFile(dir, "dummy", ".conf")
        val dir2  = Files.createTempDirectory(dir, "dir2")
        val temp3 = Files.createTempFile(dir2, "nested", ".conf")
        val temp4 = Files.createTempFile(dir2, "nested", ".conf")

        val temp = Files.createTempFile("dummy", ".conf")

        val opt = CliOptions(
          version = "2.0.0-RC5",
          customFiles = Seq(dir.toFile, temp.toFile)
        )
        opt.getFiles should contain theSameElementsAs Seq(
          temp1.toFile,
          temp2.toFile,
          temp3.toFile,
          temp4.toFile,
          temp.toFile
        )
      }
    }

    describe("getPrintStream") {
      it("should return PrintStream to specified file.") {
        val temp = Files.createTempFile("dummy", ".conf")
        val opt = CliOptions(
          version = "2.0.0-RC5",
          output = Some(temp)
        )
        opt.getPrintStream.println("test")

        val content = new String(Files.readAllBytes(temp))
        content.contains("test") shouldBe true
      }
    }
  }
}
