import sbt._
import Keys._

object Yields extends Build {

  lazy val server = (project in file(".")).settings(

    name := "Yields-server",
    organization := "yields",
    version := "0.1.0",

    scalaVersion := "2.11.7",
    scalacOptions ++= Seq(
      "-deprecation",
      "-feature",
      "-unchecked",
      "-Xfatal-warnings",
      "-Xlint:_"
    ),

    libraryDependencies ++= {
      import Dependencies._
      config ++ akka ++ akkaStream ++ orientDB
    },

    cancelable in Global := true

  )

}