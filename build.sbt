import Dependencies._

val scala213 = "2.13.5"
val scala212 = "2.12.13"

ThisBuild / organization := "com.github.vitaliihonta"
ThisBuild / scalaVersion := scala213
ThisBuild / version := "0.1.0-SNAPSHOT"

val baseSettings = Seq(
  scalacOptions ++= Seq(
    "-language:implicitConversions",
    "-language:higherKinds",
    "-Xsource:2.13"
  )
)

lazy val root = project
  .in(file("."))
  .settings(baseSettings)
  .settings(publishArtifact := false)
  .aggregate(
    sensitive.projectRefs ++
      examples.projectRefs: _*
  )

lazy val examples =
  projectMatrix
    .in(file("examples"))
    .dependsOn(sensitive)
    .settings(baseSettings)
    .settings(libraryDependencies ++= {
      Seq(
        Circe.generic,
        Logstage.core,
        Logstage.renderingCirce,
        Phobos.core
      )
    })
    .jvmPlatform(scalaVersions = List(scala212, scala213))

lazy val sensitive =
  projectMatrix
    .in(file("sensitive"))
    .settings(baseSettings)
    .settings(
      libraryDependencies ++= Seq(
        "org.scala-lang" % "scala-reflect" % scalaVersion.value % Provided,
        Circe.core       % Optional,
        Logstage.core    % Optional,
        Phobos.core      % Optional,
        Testing.scalatest
      )
    )
    .jvmPlatform(scalaVersions = List(scala212, scala213))
