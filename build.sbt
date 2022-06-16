import Dependencies._

val scala212 = "2.12.15"
val scala213 = "2.13.8"

val allScalaVersions = List(scala212, scala213)

ThisBuild / scalaVersion  := scala213
ThisBuild / organization  := "com.github.vitaliihonta"
ThisBuild / version       := "0.1.0-RC1"
ThisBuild / versionScheme := Some("early-semver")

val publishSettings = Seq(
  publishTo            := sonatypePublishToBundle.value,
  publishMavenStyle    := true,
  sonatypeProfileName  := "com.github.vitaliihonta",
  organizationHomepage := Some(url("https://github.com/vitaliihonta")),
  homepage             := Some(url("https://github.com/vitaliihonta")),
  licenses := Seq(
    "Apache 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")
  ),
  scmInfo := Some(
    ScmInfo(
      url(s"https://github.com/vitaliihonta/sensitive"),
      s"scm:git:https://github.com/vitaliihonta/sensitive.git",
      Some(s"scm:git:git@github.com:vitaliihonta/sensitive.git")
    )
  ),
  developers := List(
    Developer(
      id = "vitaliihonta",
      name = "Vitalii Honta",
      email = "vitalii.honta@gmail.com",
      url = new URL("https://github.com/vitaliihonta")
    )
  ),
  sonatypeCredentialHost := "oss.sonatype.org"
)

val baseProjectSettings = Seq(
  scalacOptions ++= Seq(
    "-language:implicitConversions",
    "-language:higherKinds",
    "-Xsource:3"
  ) ++ {
    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((3, _)) => Seq("-Ykind-projector")
      case _            => Nil
    }
  },
  ideSkipProject := scalaVersion.value == scala212
)

val coverageSettings = Seq(
  //  Keys.fork in org.jacoco.core.
  jacocoAggregateReportSettings := JacocoReportSettings(
    title = "Sensitive Coverage Report",
    subDirectory = None,
    thresholds = JacocoThresholds(),
    formats = Seq(JacocoReportFormats.ScalaHTML, JacocoReportFormats.XML), // note XML formatter
    fileEncoding = "utf-8"
  )
)

val crossCompileSettings: Seq[Def.Setting[_]] = {
  def crossVersionSetting(config: Configuration) =
    (config / unmanagedSourceDirectories) += {
      val sourceDir = (config / sourceDirectory).value
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((3, _))            => sourceDir / "scala-3"
        case Some((2, n)) if n >= 13 => sourceDir / "scala-2.13+"
        case _                       => sourceDir / "scala-2.13-"
      }
    }

  Seq(
    crossVersionSetting(Compile),
    crossVersionSetting(Test)
  )
}

val noPublishSettings = Seq(
  publish / skip := true,
  publish        := {}
)

val baseSettings    = baseProjectSettings
val baseLibSettings = baseSettings ++ publishSettings ++ coverageSettings

lazy val root = project
  .in(file("."))
  .settings(baseSettings)
  .settings(publishArtifact := false)
  .aggregate(
    sensitive.projectRefs ++
      examples.projectRefs: _*
  )
  .aggregate(coverage)

lazy val coverage = project
  .in(file("./.coverage"))
  .settings(baseSettings, coverageSettings, noPublishSettings)
  .settings(
    publish / skip := true,
    publish        := {}
  )
  .aggregate(
    sensitive.jvm(scala213)
  )

lazy val examples =
  projectMatrix
    .in(file("examples"))
    .dependsOn(sensitive)
    .settings(baseSettings, noPublishSettings)
    .settings(libraryDependencies ++= {
      Seq(
        Circe.generic,
        Logstage.core,
        Logstage.renderingCirce
      )
    })
    .jvmPlatform(scalaVersions = List(scala212, scala213))

lazy val sensitive =
  projectMatrix
    .in(file("sensitive"))
    .settings(baseLibSettings)
    .settings(
      libraryDependencies ++= Seq(
        "org.scala-lang" % "scala-reflect" % scalaVersion.value % Provided,
        Circe.core       % Optional,
        Logstage.core    % Optional,
        Testing.scalatest
      )
    )
    .jvmPlatform(scalaVersions = List(scala212, scala213))

// MISC
Global / excludeLintKeys ++= Set(
  ideSkipProject,
  jacocoAggregateReportSettings
)
