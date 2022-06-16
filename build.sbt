organization := "com.github.losizm"
name         := "pretty-metrics"
version      := "0.2.0"
description  := "A Scala veneer for Metrics"
licenses     := List("Apache License, Version 2" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt"))
homepage     := Some(url("https://github.com/losizm/little-metrics"))

versionScheme := Some("early-semver")

scalaVersion := "3.1.2"

scalacOptions := Seq("-deprecation", "-feature", "-new-syntax", "-Werror", "-Yno-experimental")

Compile / doc / scalacOptions := Seq(
  "-project", "Pretty Metrics",
  "-project-version", version.value
)

libraryDependencies ++= Seq(
  "io.dropwizard.metrics" %  "metrics-core" % "4.2.9"  % Provided,
  "org.scalatest"         %% "scalatest"    % "3.2.12" % Test
)

scmInfo := Some(
  ScmInfo(
    url("https://github.com/losizm/little-metrics"),
    "scm:git@github.com:losizm/little-metrics.git"
  )
)

developers := List(
  Developer(
    id    = "losizm",
    name  = "Carlos Conyers",
    email = "carlos.conyers@hotmail.com",
    url   = url("https://github.com/losizm")
  )
)

pomIncludeRepository := { _ => false }

publishTo := {
  val nexus = "https://oss.sonatype.org"
  isSnapshot.value match {
    case true  => Some("snaphsots" at s"$nexus/content/repositories/snapshots")
    case false => Some("releases"  at s"$nexus/service/local/staging/deploy/maven2")
  }
}

publishMavenStyle := true
