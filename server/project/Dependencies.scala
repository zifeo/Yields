import sbt._

object Dependencies {

  val typeSafe = Seq(
    "com.typesafe" % "config" % "1.3.0"
  )

  val akka = {
    val version = "2.4.0"
    Seq(
      "com.typesafe.akka" %% "akka-actor" % version,
      "com.typesafe.akka" %% "akka-slf4j" % version,
      "com.typesafe.akka" %% "akka-testkit" % version % "test"
    )
  }

  val akkaStream = {
    val version = "1.0"
    Seq(
      "com.typesafe.akka" %% "akka-stream-experimental" % version,
      "com.typesafe.akka" %% "akka-http-spray-json-experimental" % version
    )
  }

  val redis = Seq(
    "net.debasishg" %% "redisclient" % "3.0"
  )

  val tests = Seq(
    "org.scalacheck" %% "scalacheck" % "1.12.5" % "test",
    "org.scalatest" %% "scalatest" % "2.2.4" % "test"
  )

}