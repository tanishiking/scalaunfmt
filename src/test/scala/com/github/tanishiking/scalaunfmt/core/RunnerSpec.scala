package com.github.tanishiking.scalaunfmt.core

import java.io.{ByteArrayOutputStream, PrintStream}
import java.nio.file.Files

import metaconfig.Conf
import org.scalatest.{FunSpec, Matchers}
import metaconfig.typesafeconfig._

class RunnerSpec extends FunSpec with Matchers {
  describe("Runner") {
    describe("run") {
      it("should select the most match scalafmt.conf") {
        val unfmtConfStr =
          """
            |align = ["more", "some"]
          """.stripMargin
        val unfmtConf = Conf.parseString(unfmtConfStr).get

        val baos = new ByteArrayOutputStream()
        val stream = new PrintStream(baos)
        val runner = new Runner(stream)


        val testScalaCode1 =
          """
            |object A {
            |  val x   = 1 // test
            |  val yyy = 2 // test
            |}
          """.stripMargin
        val testScalaCode2 =
          """
            |object B {
            |  val xx  = 1 // test
            |  val yyy = 2 // test
            |}
          """.stripMargin

        val file1 = Files.createTempFile("test", ".scala")
        val file2 = Files.createTempFile("test", ".scala")
        Files.write(file1, testScalaCode1.getBytes)
        Files.write(file2, testScalaCode2.getBytes)

        val optimized = runner.run(Seq(file1.toFile, file2.toFile), "2.0.0-RC5", unfmtConf).right.get
        val align = Conf.parseString(optimized).get.get[String]("align").get
        align shouldBe "more"
      }
    }

    describe("validateConfigs") {

      it("should error if configs contain invalid one") {
        import Runner.ConfOps

        val configs = Seq(
          Conf.fromMap(Map(
            "version" -> Conf.fromString("2.0.0-RC5"),
          )),
          Conf.fromMap(Map(
            "version" -> Conf.fromString("2.0.0-RC5"),
            "invalidKey" -> Conf.fromString("invalidValue")
          )),
        )

        val baos = new ByteArrayOutputStream()
        val stream = new PrintStream(baos)
        val runner = new Runner(stream)

        val confFiles = configs.map(_.writeToTempFile("test", "conf"))

        runner.validateConfigs(confFiles).isLeft shouldBe true
        // new String(baos.toByteArray).contains("invalidKey") shouldBe true
      }

      it("should return Right for valid configs") {
        import Runner.ConfOps

        val configs = Seq(
          Conf.fromMap(Map(
            "version" -> Conf.fromString("2.0.0-RC5"),
          )),
          Conf.fromMap(Map(
            "version" -> Conf.fromString("2.0.0-RC5"),
            "align" -> Conf.fromString("more"),
          )),
          Conf.fromMap(Map(
            "version" -> Conf.fromString("2.0.0-RC5"),
            "align" -> Conf.fromString("more"),
          )),
        )

        val baos = new ByteArrayOutputStream()
        val stream = new PrintStream(baos)
        val runner = new Runner(stream)

        val confFiles = configs.map(_.writeToTempFile("test", "conf"))

        runner.validateConfigs(confFiles) shouldBe Right(confFiles)
      }

    }
  }

  describe("ConfOps") {
    import Runner.ConfOps

    describe("writeToTempFile") {
      it("should write the content to temporary file as hocon style") {
        val conf = Conf.fromMap(
          Map(
            "a" -> Conf.fromString("xxx"),
            "b" -> Conf.fromString("yyy"),
            "c" -> Conf.fromList(List(
              Conf.fromString("a"),
              Conf.fromString("b"),
              Conf.fromString("c"),
            ))
          )
        )
        val file = conf.writeToTempFile("test", "conf")
        val parsed = Conf.parseFile(file).get
        parsed shouldBe conf
      }
    }

    describe("combination") {
      it("should return Left for non-map configuration") {
        val c1 = Conf.fromList(List(Conf.fromString("a")))
        val c2 = Conf.fromString("test")
        val c3 = Conf.fromInt(1)
        Seq(c1, c2, c3).foreach { conf =>
          conf.combination("2.0.0").isLeft shouldBe true
        }
      }

      it("should return error for config that contains non-list value") {
        val c = Conf.fromMap(Map(
          "a" -> Conf.fromList(List(
            Conf.fromInt(1),
            Conf.fromInt(2),
          )),
          "b" -> Conf.fromList(List(
            Conf.fromString("xxx"),
            Conf.fromString("yyy")
          )),
          "c" -> Conf.fromString("non-list")
        ))
        c.combination("2.0.0").isLeft shouldBe true
      }

      it("should return error if conf contains empty list") {
        val c = Conf.fromMap(Map(
          "a" -> Conf.fromList(List(
            Conf.fromInt(1),
            Conf.fromInt(2),
          )),
          "b" -> Conf.fromList(List()),
        ))
        c.combination("2.0.0").isLeft shouldBe true
      }

      it("should return all the combinations of configurations") {
        val conf = Conf.fromMap(Map(
          "a" -> Conf.fromList(List(
            Conf.fromString("a1"),
            Conf.fromString("a2"),
          )),
          "b" -> Conf.fromList(List(
            Conf.fromString("b1"),
            Conf.fromString("b2"),
            Conf.fromString("b3"),
          )),
          "c" -> Conf.fromList(List(
            Conf.fromString("c1"),
          )),
        ))
        conf.combination("2.0.0").right.get should contain theSameElementsAs
          Seq(
            Conf.fromMap(Map(
              "version" -> Conf.fromString("2.0.0"),
              "a" -> Conf.fromString("a1"),
              "b" -> Conf.fromString("b1"),
              "c" -> Conf.fromString("c1"),
            )),
            Conf.fromMap(Map(
              "version" -> Conf.fromString("2.0.0"),
              "a" -> Conf.fromString("a1"),
              "b" -> Conf.fromString("b2"),
              "c" -> Conf.fromString("c1"),
            )),
            Conf.fromMap(Map(
              "version" -> Conf.fromString("2.0.0"),
              "a" -> Conf.fromString("a1"),
              "b" -> Conf.fromString("b3"),
              "c" -> Conf.fromString("c1"),
            )),
            Conf.fromMap(Map(
              "version" -> Conf.fromString("2.0.0"),
              "a" -> Conf.fromString("a2"),
              "b" -> Conf.fromString("b1"),
              "c" -> Conf.fromString("c1"),
            )),
            Conf.fromMap(Map(
              "version" -> Conf.fromString("2.0.0"),
              "a" -> Conf.fromString("a2"),
              "b" -> Conf.fromString("b2"),
              "c" -> Conf.fromString("c1"),
            )),
            Conf.fromMap(Map(
              "version" -> Conf.fromString("2.0.0"),
              "a" -> Conf.fromString("a2"),
              "b" -> Conf.fromString("b3"),
              "c" -> Conf.fromString("c1"),
            )),
          )
      }

      it("should return the combinations of configs (for list of list)") {
        val confA1 = Conf.fromList(List(
          Conf.fromString("a1"),
          Conf.fromString("a2"),
          Conf.fromString("a3"),
        ))
        val confA2 = Conf.fromList(List(
          Conf.fromString("a1"),
          Conf.fromString("a2"),
        ))

        val confB1 = Conf.fromMap(Map(
          "b1" -> Conf.fromString("b1"),
          "b2" -> Conf.fromString("b2"),
        ))
        val confB2 = Conf.fromMap(Map(
          "b1" -> Conf.fromString("b3"),
          "b2" -> Conf.fromString("b4"),
        ))

        val conf = Conf.fromMap(Map(
          "a" -> Conf.fromList(List(
            confA1,
            confA2
          )),
          "b" -> Conf.fromList(List(
            confB1,
            confB2
          )),
        ))

        conf.combination("2.0.0").right.get should contain theSameElementsAs
          Seq(
            Conf.fromMap(Map(
              "version" -> Conf.fromString("2.0.0"),
              "a" -> confA1,
              "b" -> confB1,
            )),
            Conf.fromMap(Map(
              "version" -> Conf.fromString("2.0.0"),
              "a" -> confA1,
              "b" -> confB2,
            )),
            Conf.fromMap(Map(
              "version" -> Conf.fromString("2.0.0"),
              "a" -> confA2,
              "b" -> confB1,
            )),
            Conf.fromMap(Map(
              "version" -> Conf.fromString("2.0.0"),
              "a" -> confA2,
              "b" -> confB2,
            )),
          )
      }

      it("should override version (TODO: candidates should be unique)") {
        val conf = Conf.fromMap(Map(
          "a" -> Conf.fromList(List(
            Conf.fromString("a1"),
            Conf.fromString("a2"),
          )),
          "version" -> Conf.fromList(List(
            Conf.fromString("xxx"),
            Conf.fromString("yyy"),
          )),
        ))
        conf.combination("2.0.0").right.get should contain theSameElementsAs
          Seq(
            Conf.fromMap(Map(
              "a" -> Conf.fromString("a1"),
              "version" -> Conf.fromString("2.0.0"),
            )),
            Conf.fromMap(Map(
              "a" -> Conf.fromString("a1"),
              "version" -> Conf.fromString("2.0.0"),
            )),
            Conf.fromMap(Map(
              "a" -> Conf.fromString("a2"),
              "version" -> Conf.fromString("2.0.0"),
            )),
            Conf.fromMap(Map(
              "a" -> Conf.fromString("a2"),
              "version" -> Conf.fromString("2.0.0"),
            )),
          )
      }
    }
  }

}
