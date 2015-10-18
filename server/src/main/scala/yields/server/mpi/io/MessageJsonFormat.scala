package yields.server.mpi.io

import spray.json._
import spray.json.DefaultJsonProtocol._
import yields.server.mpi.{ParseMessageException, GroupMessage, UserUpdate, Message}

object MessageJsonFormat extends RootJsonFormat[Message] {

  def packWithKind[T: JsonWriter](obj: T): JsValue =
    JsObject(
      "kind" -> obj.getClass.getSimpleName.toJson,
      "message" -> obj.toJson
    )

  override def write(obj: Message): JsValue = obj match {
    case x: GroupMessage => packWithKind(x)
    case x: UserUpdate => packWithKind(x)
  }

  override def read(json: JsValue): Message = {
    val fields = json.asJsObject.fields
    val raw = fields("message")
    fields("kind") match {
      case JsString("GroupMessage") => raw.convertTo[GroupMessage]
      case JsString("UserUpdate") => raw.convertTo[UserUpdate]
      case _ => ParseMessageException()
    }
  }

}