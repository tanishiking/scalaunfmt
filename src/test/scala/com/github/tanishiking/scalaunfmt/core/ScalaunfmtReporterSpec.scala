package com.github.tanishiking.scalaunfmt.core

import java.io.{ByteArrayOutputStream, PrintStream}
import java.nio.file.Paths

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class ScalaunfmtReporterSpec extends AnyFunSpec with Matchers {
  describe("ScalaunfmtReporter") {
    it("should not print error to PrintStream") {
      val baos   = new ByteArrayOutputStream()
      val stream = new PrintStream(baos)

      val message  = "test message"
      val reporter = new ScalaunfmtReporter(stream)
      reporter.error(Paths.get("/dummy"), message)

      baos.toString.contains(message) shouldBe false
    }

    describe(".error") {
      it("should return None if error() was not called") {
        val baos     = new ByteArrayOutputStream()
        val stream   = new PrintStream(baos)
        val reporter = new ScalaunfmtReporter(stream)
        reporter.error shouldBe None
      }

      it("should return all the messages") {
        val baos   = new ByteArrayOutputStream()
        val stream = new PrintStream(baos)

        val message1 = "message1"
        val message2 = "message2"
        val message3 = "message3"

        val reporter = new ScalaunfmtReporter(stream)
        reporter.error(Paths.get("/dummy"), message1)
        reporter.error(Paths.get("/dummy"), message2)
        reporter.error(Paths.get("/dummy"), message3)

        baos.toString.contains(message1) shouldBe false
        baos.toString.contains(message2) shouldBe false
        baos.toString.contains(message3) shouldBe false

        val message = reporter.error.get.getMessage
        message.contains(message1) shouldBe true
        message.contains(message2) shouldBe true
        message.contains(message3) shouldBe true
      }
    }

  }
}
