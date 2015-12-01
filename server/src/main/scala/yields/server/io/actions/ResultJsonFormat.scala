package yields.server.io.actions

import spray.json.DefaultJsonProtocol._
import spray.json._
import yields.server.actions._
import yields.server.actions.groups._
import yields.server.actions.nodes.{NodeHistoryRes, NodeSearchRes}
import yields.server.actions.users._
import yields.server.io._

/** Json format for [[Result]]. */
object ResultJsonFormat extends RootJsonFormat[Result] {

  private val kindFld = "kind"
  private val messageFld = "message"

  /**
    * Serializes the result of an action in a json format.
    * @param obj the result of an action
    * @return corresponding json object
    */
  override def write(obj: Result): JsValue = {
    val kind = obj.getClass.getSimpleName
    obj match {
      case x: GroupCreateRes => packWithKind(x)
      case x: GroupUpdateRes => packWithKind(x)
      case x: GroupInfoRes => packWithKind(x)
      case x: GroupMessageRes => packWithKind(x)

      case x: NodeHistoryRes => packWithKind(x)
      case x: NodeSearchRes => packWithKind(x)

      case x: UserConnectRes => packWithKind(x)
      case x: UserUpdateRes => packWithKind(x)
      case x: UserGroupListRes => packWithKind(x)
      case x: UserInfoRes => packWithKind(x)
      case x: UserSearchRes => packWithKind(x)

      case _ => serializationError(s"unregistered result kind: $kind")
    }
  }

  /**
    * Format the message with its message type.
    * @param obj message to pack
    * @tparam T message type
    * @return packed message json format
    */
  def packWithKind[T: JsonWriter](obj: T): JsValue = JsObject(
    kindFld -> obj.getClass.getSimpleName.toJson,
    messageFld -> obj.toJson
  )

  override def read(json: JsValue): Result =
    json.asJsObject.getFields(kindFld, messageFld) match {
      case Seq(JsString(kind), message) =>
        kind match {
          case "GroupCreateRes" => message.convertTo[GroupCreateRes]
          case "GroupUpdateRes" => message.convertTo[GroupUpdateRes]
          case "GroupInfoRes" => message.convertTo[GroupInfoRes]
          case "GroupMessageRes" => message.convertTo[GroupMessageRes]

          case "NodeHistoryRes" => message.convertTo[NodeHistoryRes]
          case "NodeSearchRes" => message.convertTo[NodeSearchRes]

          case "UserConnectRes" => message.convertTo[UserConnectRes]
          case "UserUpdateRes" => message.convertTo[UserUpdateRes]
          case "UserGroupListRes" => message.convertTo[UserGroupListRes]
          case "UserInfoRes" => message.convertTo[UserInfoRes]
          case "UserSearchRes" => message.convertTo[UserSearchRes]

          case _ => deserializationError(s"unregistered result kind: $kind")
        }
      case _ => deserializationError(s"bad result format: $json")
    }

}