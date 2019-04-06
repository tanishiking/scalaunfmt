package com.github.tanishiking.scalaunfmt
package core

import java.io.{File, PrintStream}
import java.nio.file.Files
import java.util.concurrent.atomic.AtomicInteger

import info.debatty.java.stringsimilarity.Levenshtein
import metaconfig.Conf
import org.scalafmt.interfaces.Scalafmt

class Runner(err: PrintStream) {
  val instance: Scalafmt = Scalafmt.create(this.getClass.getClassLoader)

  def run(files: Seq[File], version: String, scalaunfmtConf: Conf): Either[Throwable, String] = {
    import Runner.ConfOps

    val levenshtein = new Levenshtein()
    val reporter    = new ScalaunfmtReporter(System.err)

    val patch = Conf.fromMap(Map("version" -> Conf.Str(version)))
    for {
      candidates <- scalaunfmtConf.combination
      cs             = candidates.map(c => Conf.applyPatch(c, patch))
      candidateFiles = cs.map(_.writeToTempFile(".scalaunfmt", ".conf"))
      validConfigs <- validateConfigs(candidateFiles)
    } yield {
      val numCandidates = candidates.length
      val processed     = new AtomicInteger()
      val pathToDistance = validConfigs.par
        .map { candidate =>
          val distance = files.map { file =>
            val original = new String(Files.readAllBytes(file.toPath))
            val formatted = instance
              .withReporter(reporter)
              .format(
                candidate.toPath,
                file.toPath,
                original
              )
            levenshtein.distance(original, formatted)
          }.sum
          val current = processed.addAndGet(1)
          err.println(s"processed: $current / $numCandidates")
          (candidate.toPath, distance)
        }
        .seq
        .toMap
      val path = pathToDistance.minBy(_._2)._1
      new String(Files.readAllBytes(path))
    }
  }

  private[core] def validateConfigs(candidates: Seq[File]): Either[Throwable, Seq[File]] = {
    val reporter = new ScalaunfmtReporter(err)

    val testScalaCode = "object A {}"
    val dummyFilePath = File.createTempFile("dummy", ".scala").toPath

    candidates.foreach { candidate =>
      instance
        .withReporter(reporter)
        .format(
          candidate.toPath,
          dummyFilePath,
          testScalaCode
        )
    }
    reporter.error.toLeft(candidates)
  }
}

object Runner {
  private[core] implicit class ConfOps(private val conf: Conf) extends AnyVal {
    def writeToTempFile(prefix: String, suffix: String): File = {
      val hocon = Conf.printHocon(conf)
      val temp  = File.createTempFile(prefix, suffix)
      Files.write(temp.toPath, hocon.getBytes)
      temp
    }

    def combination: Either[Throwable, Seq[Conf]] = {
      for {
        ls <- conf match {
          case Conf.Obj(value) => Right(value)
          case _ =>
            Left(
              new IllegalArgumentException(s"Invalid configuration, it must be obj: ${conf.show}")
            )
        }
        kvss <- ls
          .map {
            case (k, Conf.Lst(cs)) if cs.nonEmpty => Right((k, cs))
            case (k, v @ Conf.Obj(vs)) if vs.nonEmpty =>
              v.combination match {
                case Right(cs) => Right((k, cs.toList))
                case Left(e)   => Left(e)
              }
            case _ =>
              Left(
                new IllegalArgumentException(
                  s"Invalid configuration, its all values must be List: ${conf.show}"
                )
              )
          }
          .foldRight(Right(Nil): Either[Throwable, List[(String, List[Conf])]]) { (elem, acc) =>
            acc.flatMap(ls => elem.map(_ :: ls))
          }
      } yield {
        combinations(kvss).map { candidate =>
          Conf.fromMap(candidate.toMap)
        }
      }
    }

    private def combinations[T](kvss: List[(String, List[T])]): List[List[(String, T)]] =
      kvss match {
        case Nil => List(Nil)
        case (k, vs) :: rss =>
          for {
            v  <- vs
            cs <- combinations(rss)
          } yield (k, v) :: cs
      }
  }
}
