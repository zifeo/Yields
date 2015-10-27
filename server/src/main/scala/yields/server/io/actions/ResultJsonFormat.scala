package yields.server.io.actions

import spray.json.DefaultJsonProtocol._
import spray.json._
import yields.server.actions._
import yields.server.actions.groups._
import yields.server.actions.users._
import yields.server.io._

/** Json format for [[Result]]. */
object ResultJsonFormat extends RootJsonFormat[Result] {

   private val kindFld = "kind"
   private val messageFld = "message"

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

   override def write(obj: Result): JsValue = {
     val kind = obj.getClass.getSimpleName
     obj match {
       case x: GroupCreateRes => packWithKind(x)
       case x: GroupUpdateRes => packWithKind(x)
       case x: GroupMessageRes => packWithKind(x)
       case x: GroupHistoryRes => packWithKind(x)

       case x: UserConnectRes => packWithKind(x)
       case x: UserUpdateRes => packWithKind(x)
       case x: UserGroupListRes => packWithKind(x)

       case _ => serializationError(s"unregistered action kind: $kind")
     }
   }

   override def read(json: JsValue): Result =
     json.asJsObject.getFields(kindFld, messageFld) match {
       case Seq(JsString(kind), message) =>
         kind match {
           case "GroupCreateRes" => message.convertTo[GroupCreateRes]
           case "GroupUpdateRes" => message.convertTo[GroupUpdateRes]
           case "GroupMessageRes" => message.convertTo[GroupMessageRes]
           case "GroupHistoryRes" => message.convertTo[GroupHistoryRes]

           case "UserConnectRes" => message.convertTo[UserConnectRes]
           case "UserUpdateRes" => message.convertTo[UserUpdateRes]
           case "UserGroupListRes" => message.convertTo[UserGroupListRes]

           case _ => deserializationError(s"unregistered action kind: $kind")
         }
       case _ => deserializationError(s"bad action format: $json")
     }

 }