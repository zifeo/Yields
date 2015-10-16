import sbt._

object Dependencies {

  val config = Seq(
    "com.typesafe" % "config" % "1.3.0"
  )

  val akka = {
    val version = "2.4.0"
    Seq(
      "com.typesafe.akka" %% "akka-actor" % version,
      "com.typesafe.akka" %% "akka-testkit" % version,
      "com.typesafe.akka" %% "akka-slf4j" % version
    )
  }

  val akkaStream = {
    val version = "1.0"
    Seq(
      "com.typesafe.akka" %% "akka-stream-experimental" % version,
      "com.typesafe.akka" %% "akka-http-spray-json-experimental" % version
    )
  }


  val orientDB = {
    val version = "2.1.4"
    Seq(
      "com.orientechnologies" % "orientdb-core" % version,
      "com.orientechnologies" % "orientdb-client" % version,
      "com.orientechnologies" % "orientdb-graphdb" % version
    )
  }

}