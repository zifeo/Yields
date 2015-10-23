package yields.server.io

import spray.json._
import yields.server.actions.exceptions.{SerializationException, ActionResultException}

/** Json writer for [[ActionResultException]]. */
object ActionExceptionJsonWriter extends RootJsonWriter[ActionResultException] {

  override def write(obj: ActionResultException): JsValue = obj match {
    case x: SerializationException => x.toJson
  }

}