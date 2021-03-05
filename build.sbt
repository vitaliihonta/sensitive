import Dependencies._

val scala213 = "2.13.5"
val scala212 = "2.12.12"

ThisBuild / organization := "com.github.vitaliihonta"
ThisBuild / scalaVersion := scala213
ThisBuild / version := "0.1.0-SNAPSHOT"

val baseSettings = Seq(
  scalacOptions ++= Seq(
    "-language:implicitConversions",
    "-language:higherKinds"
  )
)

lazy val root = project
  .in(file("."))
  .settings(baseSettings)
  .settings(publishArtifact := false)
  .aggregate(
    `sensitive-core`.projectRefs ++
      `sensitive-circe`.projectRefs ++
      examples.projectRefs: _*
  )

lazy val examples =
  projectMatrix
    .in(file("examples"))
    .dependsOn(`sensitive-core`, `sensitive-circe`)
    .settings(baseSettings)
    .settings(libraryDependencies ++= {
      Seq(Circe.generic)
    })
    .jvmPlatform(scalaVersions = List(scala212, scala213))

lazy val `sensitive-core` =
  projectMatrix
    .in(file("sensitive-core"))
    .settings(baseSettings)
    .settings(
      libraryDependencies ++= Seq(
        "org.scala-lang" % "scala-reflect" % scalaVersion.value % Provided,
        Testing.scalatest
      )
    )
    .jvmPlatform(scalaVersions = List(scala212, scala213))

lazy val `sensitive-circe` =
  projectMatrix
    .in(file("sensitive-circe"))
    .dependsOn(`sensitive-core`)
    .settings(baseSettings)
    .settings(libraryDependencies ++= Seq(Circe.core, Testing.scalatest))
    .jvmPlatform(scalaVersions = List(scala212, scala213))
