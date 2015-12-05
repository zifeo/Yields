package yields.server.io.actions

import spray.json.DefaultJsonProtocol._
import spray.json._
import yields.server.actions._
import yields.server.actions.groups._
import yields.server.actions.nodes.NodeHistoryRes
import yields.server.actions.publisher.{PublisherMessageBrd, PublisherUpdateBrd, PublisherMessage, PublisherCreateBrd}
import yields.server.actions.users._
import yields.server.io._

/** Json format for [[Broadcast]]. */
object BroadcastJsonFormat extends RootJsonFormat[Broadcast] {

  private val kindFld = "kind"
  private val messageFld = "message"

  /**
    * Serializes the broadcast of an action in a json format.
    * @param obj the broadcast of an action
    * @return corresponding json object
    */
  override def write(obj: Broadcast): JsValue = {
    val kind = obj.getClass.getSimpleName
    obj match {
      case x: GroupCreateBrd => packWithKind(x)
      case x: GroupUpdateBrd => packWithKind(x)
      case x: GroupMessageBrd => packWithKind(x)

      case x: PublisherCreateBrd => packWithKind(x)
      case x: PublisherUpdateBrd => packWithKind(x)
      case x: PublisherMessageBrd => packWithKind(x)

      case x: UserUpdateBrd => packWithKind(x)

      case _ => serializationError(s"unregistered broadcast kind: $kind")
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

  override def read(json: JsValue): Broadcast =
    json.asJsObject.getFields(kindFld, messageFld) match {
      case Seq(JsString(kind), message) =>
        kind match {
          case "GroupCreateBrd" => message.convertTo[GroupCreateBrd]
          case "GroupUpdateBrd" => message.convertTo[GroupUpdateBrd]
          case "GroupMessageBrd" => message.convertTo[GroupMessageBrd]

          case "PublisherCreateBrd" => message.convertTo[PublisherCreateBrd]
          case "PublisherUpdateBrd" => message.convertTo[PublisherUpdateBrd]
          case "PublisherMessageBrd" => message.convertTo[PublisherMessageBrd]

          case "UserUpdateBrd" => message.convertTo[UserUpdateBrd]

          case _ => deserializationError(s"unregistered broadcast kind: $kind")
        }
      case _ => deserializationError(s"bad broadcast format: $json")
    }

}