lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "emiliomg",
      scalaVersion := "2.13.0",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "Hello World",
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.0.8" % Test,
      "org.scalacheck" %% "scalacheck" % "1.14.0" % Test
    ),
    scalacOptions += "-deprecation"
  )
