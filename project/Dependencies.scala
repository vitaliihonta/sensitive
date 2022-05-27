import sbt._

object Dependencies {

  object Circe {
    private val circeVersion = "0.12.3"

    val core    = "io.circe" %% "circe-core"    % circeVersion
    val generic = "io.circe" %% "circe-generic" % circeVersion
  }

  object Logstage {
    private val izumiVersion = "1.0.3"

    val core           = "io.7mind.izumi" %% "logstage-core"            % izumiVersion
    val renderingCirce = "io.7mind.izumi" %% "logstage-rendering-circe" % izumiVersion
  }

  object Testing {
    val scalatest = "org.scalatest" %% "scalatest" % "3.2.5" % Test
  }
}
