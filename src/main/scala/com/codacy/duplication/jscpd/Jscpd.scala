package com.codacy.duplication.jscpd

import better.files.File._
import com.codacy.duplication.scala.seed.DockerDuplication
import com.codacy.plugins.api.Options.{Key, Value}
import com.codacy.plugins.api.Source
import com.codacy.plugins.api.duplication.{
  DuplicationClone,
  DuplicationCloneFile,
  DuplicationTool
}
import com.codacy.plugins.api.languages.Language
import play.api.libs.json._

import scala.sys.process._
import scala.util.Try

object Jscpd extends DuplicationTool {

  def apply(
      path: Source.Directory,
      language: Option[Language],
      options: Map[Key, Value]
  ): Try[List[DuplicationClone]] = {
    val minTokens = options
      .get(Key("minTokenMatch"))
      .flatMap(_.asOpt[Int])
      .getOrElse(40)
      .toString
    Try {
      val pattern = language match {
        case Some(l) => List("--pattern", s"**/*${l.extensions.mkString("|")}")
        case None    => List.empty
      }
      temporaryDirectory() { reportDir =>
        val command = "/node_modules/jscpd/bin/jscpd" ::
          "--min-tokens" ::
          minTokens ::
          "--reporters" ::
          "json" ::
          "--output" ::
          reportDir.pathAsString :: "." :: pattern
        command.!(
          ProcessLogger(fout = System.err.println, ferr = System.err.println)
        )
        val reportFile = reportDir / "jscpd-report.json"
        if (reportFile.exists) {
          val json =
            Json.parse((reportDir / "jscpd-report.json").contentAsString)
          json("duplicates")
            .as[JsArray]
            .value
            .view
            .map { duplicate =>
              DuplicationClone(
                cloneLines = duplicate("fragment").as[String],
                nrTokens = duplicate("tokens").as[Int],
                nrLines = duplicate("lines").as[Int],
                files = Seq("firstFile", "secondFile").map((f: String) => {
                  val o = duplicate(f)
                  val filePath = o("name").as[String]
                  val startLine = o("start").as[Int]
                  val endLine = o("end").as[Int]
                  DuplicationCloneFile(
                    filePath = filePath,
                    startLine = startLine,
                    endLine = endLine
                  )
                })
              )
            }
            .toList
        } else {
          List.empty
        }
      }
    }
  }
}

object Main extends DockerDuplication(Jscpd)()
