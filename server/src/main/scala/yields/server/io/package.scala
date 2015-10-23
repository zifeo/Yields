package yields.server

import spray.json.DefaultJsonProtocol._
import yields.server.actions.exceptions.SerializationException
import yields.server.actions.groups.{GroupCreate, GroupHistory, GroupMessage, GroupUpdate}
import yields.server.actions.users.{UserConnect, UserGroupList, UserUpdate}

/**
 * Provides json format for read/write specification.
 */
package object io {

  implicit val actionJsonFormat = ActionJsonFormat
  implicit val actionExceptionJsonWriter = ActionExceptionJsonWriter

  /***** Actions *****/

  implicit val groupCreateJsonFormat = jsonFormat2(GroupCreate)
  implicit val groupUpdateJsonFormat = jsonFormat3(GroupUpdate)
  implicit val groupMessageJsonFormat = jsonFormat2(GroupMessage)
  implicit val groupHistoryJsonFormat = jsonFormat3(GroupHistory)

  implicit val userConnectJsonFormat = jsonFormat1(UserConnect)
  implicit val userGroupListJsonFormat = jsonFormat1(UserGroupList)
  implicit val userUpdateJsonFormat = jsonFormat4(UserUpdate)

  /***** Action exceptions *****/

  implicit val serializationExceptionJsonFormat = jsonFormat1(SerializationException)

}
