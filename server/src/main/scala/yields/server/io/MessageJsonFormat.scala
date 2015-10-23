package yields.server.io

import spray.json.DefaultJsonProtocol._
import spray.json._
import yields.server.io._
import yields.server.actions._
import yields.server.actions.exceptions.{SerializationActionException, ActionException}
import yields.server.actions.groups.{GroupHistory, GroupUpdate, GroupCreate, GroupMessage}
import yields.server.actions.users.{UserGroupList, UserConnect, UserUpdate}

import scala.util.{Failure, Success, Try}

/** Json format for [[Action]]. */
object MessageJsonFormat extends RootJsonFormat[Action] {

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

  override def write(obj: Action): JsValue = obj match {
    case x: GroupCreate => packWithKind(x)
    case x: GroupUpdate => packWithKind(x)
    case x: GroupMessage => packWithKind(x)
    case x: GroupHistory => packWithKind(x)
    case x: UserConnect => packWithKind(x)
    case x: UserUpdate => packWithKind(x)
    case x: UserGroupList => packWithKind(x)
    case x: ActionException => packWithKind(x)
    case x => packWithKind(SerializationActionException(s"unknown message: $x"))
  }

  override def read(json: JsValue): Action = Try(json.asJsObject.fields) match {
    case Success(flds) if flds.contains(kindFld) && flds.contains(messageFld) && flds.contains(metadataFld) =>
      val raw = flds(messageFld)
      flds(kindFld) match {
        case JsString("GroupCreate") => raw.convertTo[GroupCreate]
        case JsString("GroupUpdate") => raw.convertTo[GroupUpdate]
        case JsString("GroupMessage") => raw.convertTo[GroupMessage]
        case JsString("GroupHistory") => raw.convertTo[GroupHistory]
        case JsString("UserConnect") => raw.convertTo[UserConnect]
        case JsString("UserUpdate") => raw.convertTo[UserUpdate]
        case JsString("UserGroupList") => raw.convertTo[UserGroupList]
        case other => SerializationActionException(s"unknown message kind: $other")
      }
    // TODO : handle metadata
    case Success(_) =>
      SerializationActionException("message object malformed")
    case Failure(exception) =>
      val message = exception.getMessage
      SerializationActionException(s"bad message format: $message")
  }

}