package yields.server.mpi.io

import spray.json._
import yields.server.mpi.exceptions.{SerializationMessageException, MessageException}

/** Json format for [[MessageException]]. */
object MessageExceptionJsonWriter extends RootJsonWriter[MessageException] {

  override def write(obj: MessageException): JsValue = obj match {
    case x: SerializationMessageException => x.toJson
  }

}