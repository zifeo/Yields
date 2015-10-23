package yields.server.io

import spray.json._
import yields.server.actions.exceptions.{SerializationActionException, ActionException}

/** Json writer for [[ActionException]]. */
object ActionExceptionJsonWriter extends RootJsonWriter[ActionException] {

  override def write(obj: ActionException): JsValue = obj match {
    case x: SerializationActionException => x.toJson
  }

}