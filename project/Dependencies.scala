import sbt._

object Dependencies {

  object Circe {
    private val circeVersion = "0.12.3"

    val core    = "io.circe" %% "circe-core"    % circeVersion
    val generic = "io.circe" %% "circe-generic" % circeVersion
  }

  object Testing {
    val scalatest = "org.scalatest" %% "scalatest" % "3.2.5" % Test
  }
}
