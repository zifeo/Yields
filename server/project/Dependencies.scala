import sbt._

object Dependencies {

  val meta = Seq(
    "com.typesafe" % "config" % "1.3.0",
    "ch.qos.logback" % "logback-classic" % "1.1.3",
    "ch.qos.logback" % "logback-access" % "1.1.3",
    "net.logstash.logback" % "logstash-logback-encoder" % "4.5.1"
  )

  val akka = {
    val version = "2.4.1"
    Seq(
      "com.typesafe.akka" %% "akka-actor" % version,
      "com.typesafe.akka" %% "akka-slf4j" % version,
      "com.typesafe.akka" %% "akka-testkit" % version % "test"
    )
  }

  val akkaExp = {
    val version = "2.0-M2"
    Seq(
      "com.typesafe.akka" %% "akka-stream-experimental" % version,
      "com.typesafe.akka" %% "akka-http-spray-json-experimental" % version,
      "com.typesafe.akka" %% "akka-stream-testkit-experimental" % version % "test"
    )
  }

  val rss = Seq(
    "com.rometools" % "rome" % "1.5.1"
  )

  val redis = Seq(
    "net.debasishg" %% "redisclient" % "3.1"
  )

  val tests = Seq(
    "org.scalacheck" %% "scalacheck" % "1.12.5" % "test",
    "org.scalatest" %% "scalatest" % "2.2.4" % "test"
  )

}