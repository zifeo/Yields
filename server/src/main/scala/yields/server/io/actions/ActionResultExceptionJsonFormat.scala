package yields.server.io.actions

import spray.json._
import yields.server.actions.exceptions.{ActionResultException, SerializationException}
import yields.server.io._

/** Json writer for [[ActionResultException]]. */
object ActionResultExceptionJsonFormat extends RootJsonFormat[ActionResultException] {

  override def read(json: JsValue): ActionResultException = ???

  override def write(obj: ActionResultException): JsValue = obj match {
    case x: SerializationException => x.toJson
  }

}