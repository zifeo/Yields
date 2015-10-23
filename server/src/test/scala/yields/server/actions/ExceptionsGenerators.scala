package yields.server.actions

import org.scalacheck.Arbitrary
import yields.server._
import yields.server.actions.exceptions.SerializationException

trait ExceptionsGenerators {

  implicit lazy val serializationExceptionArb: Arbitrary[SerializationException] = Arbitrary {
    for {
      message <- cleanStringGen
    } yield SerializationException(message)
  }

}
