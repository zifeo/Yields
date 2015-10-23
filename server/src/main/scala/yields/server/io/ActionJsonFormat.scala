package yields.server.io

import spray.json.DefaultJsonProtocol._
import spray.json._
import yields.server.actions._
import yields.server.actions.exceptions.{SerializationException, ActionResultException}
import yields.server.actions.groups.{GroupHistory, GroupUpdate, GroupCreate, GroupMessage}
import yields.server.actions.users.{UserGroupList, UserConnect, UserUpdate}

import scala.util.{Failure, Success, Try}

/** Json format for [[Action]]. */
object ActionJsonFormat extends RootJsonFormat[Action] {

  private val kindFld = "kind"
  private val messageFld = "message"
  private val metadataFld = "metadata"

  /**
   * Format the action with its type.
   * @param obj action to pack
   * @tparam T action type
   * @return packed action json format
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
    case x: ActionResultException => packWithKind(x)
    case x => packWithKind(SerializationException(s"unknown action: $x"))
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
        case other => SerializationException(s"unknown action kind: $other")
      }
    // TODO : handle metadata
    case Success(_) =>
      SerializationException("action type malformed")
    case Failure(exception) =>
      val message = exception.getMessage
      SerializationException(s"bad action format: $message")
  }

}