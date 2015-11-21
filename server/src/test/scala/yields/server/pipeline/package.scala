package yields.server

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

package object pipeline {

  implicit lazy val system = ActorSystem("Yields-server-test")
  implicit lazy val materializer = ActorMaterializer()

}
