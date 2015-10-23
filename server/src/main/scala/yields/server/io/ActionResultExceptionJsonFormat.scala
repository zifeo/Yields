package yields.server.io

import spray.json._
import yields.server.actions.exceptions.{ActionResultException, SerializationException}

/** Json writer for [[ActionResultException]]. */
object ActionResultExceptionJsonFormat extends RootJsonFormat[ActionResultException] {

  override def read(json: JsValue): ActionResultException = ???

  override def write(obj: ActionResultException): JsValue = obj match {
    case x: SerializationException => x.toJson
  }

}