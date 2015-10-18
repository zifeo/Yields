package yields.server.mpi.io

import spray.json.DefaultJsonProtocol._
import spray.json._
import yields.server.mpi._
import yields.server.mpi.exceptions.{SerializationMessageException, MessageException}
import yields.server.mpi.groups.{GroupCreate, GroupMessage}
import yields.server.mpi.users.UserUpdate

import scala.util.{Failure, Success, Try}

/** Json format for [[Message]]. */
object MessageJsonFormat extends RootJsonFormat[Message] {

  private val kindFld = "kind"
  private val messageFld = "message"
  private val metadataFld = "metadata"

  /**
   * Format the message with its message type.
   * @param obj message to pack
   * @tparam T message type
   * @return packed message json format
   */
  def packWithKind[T: JsonWriter](obj: T): JsValue =
    JsObject(
      kindFld -> obj.getClass.getSimpleName.toJson,
      messageFld -> obj.toJson,
      metadataFld -> JsNull // TODO : handle metadata
    )

  override def write(obj: Message): JsValue = obj match {
    case x: GroupCreate => packWithKind(x)
    case x: GroupMessage => packWithKind(x)
    case x: UserUpdate => packWithKind(x)
    case x: MessageException => packWithKind(x)
    case x => packWithKind(SerializationMessageException(s"unknown message: $x"))
  }

  override def read(json: JsValue): Message = Try(json.asJsObject.fields) match {
    case Success(flds) if flds.contains(kindFld) && flds.contains(messageFld) && flds.contains(metadataFld) =>
      val raw = flds(messageFld)
      flds(kindFld) match {
        case JsString("GroupCreate") => raw.convertTo[GroupCreate]
        case JsString("GroupMessage") => raw.convertTo[GroupMessage]
        case JsString("UserUpdate") => raw.convertTo[UserUpdate]
        case other => SerializationMessageException(s"unknown message kind: $other")
      }
    // TODO : handle metadata
    case Success(_) =>
      SerializationMessageException("message object malformed")
    case Failure(exception) =>
      val message = exception.getMessage
      SerializationMessageException(s"bad message format: $message")
  }

}