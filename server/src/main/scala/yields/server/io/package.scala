package yields.server

import spray.json.DefaultJsonProtocol._
import yields.server.actions.exceptions.SerializationActionException
import yields.server.actions.groups.{GroupHistory, GroupUpdate, GroupCreate, GroupAction}
import yields.server.actions.users.{UserGroupList, UserConnect, UserUpdate}
import yields.server.io.{ActionExceptionJsonWriter, MessageJsonFormat}

/**
 * Provides json format for read/write specification.
 */
package object io {

  implicit val actionJsonFormat = MessageJsonFormat
  implicit val actionExceptionJsonWriter = ActionExceptionJsonWriter

  // Message subclasses (do not forgot to add class/name binding in message json format superclass)
  implicit val groupCreateJsonFormat = jsonFormat2(GroupCreate)
  implicit val groupUpdateJsonFormat = jsonFormat3(GroupUpdate)
  implicit val groupMessageJsonFormat = jsonFormat2(GroupAction)
  implicit val groupHistoryJsonFormat = jsonFormat3(GroupHistory)

  implicit val userConnectJsonFormat = jsonFormat1(UserConnect)
  implicit val userGroupListJsonFormat = jsonFormat1(UserGroupList)
  implicit val userUpdateJsonFormat = jsonFormat4(UserUpdate)

  // Message exception
  implicit val parseMessageExceptionJsonFormat = jsonFormat1(SerializationActionException)

}
