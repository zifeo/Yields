package yields.server.mpi

import spray.json.DefaultJsonProtocol._
import yields.server.mpi.exceptions.SerializationMessageException
import yields.server.mpi.groups.{GroupHistory, GroupUpdate, GroupCreate, GroupMessage}
import yields.server.mpi.users.{UserGroupList, UserConnect, UserUpdate}

/**
 * Provides json format for read/write specification.
 */
package object io {

  implicit val messageJsonFormat = MessageJsonFormat
  implicit val messageExceptionJsonWriter = MessageExceptionJsonWriter

  // Message subclasses (do not forgot to add class/name binding in message json format superclass)
  implicit val groupCreateJsonFormat = jsonFormat2(GroupCreate)
  implicit val groupUpdateJsonFormat = jsonFormat3(GroupUpdate)
  implicit val groupMessageJsonFormat = jsonFormat2(GroupMessage)
  implicit val groupHistoryJsonFormat = jsonFormat3(GroupHistory)

  implicit val userConnectJsonFormat = jsonFormat1(UserConnect)
  implicit val userUpdateJsonFormat = jsonFormat4(UserUpdate)
  implicit val userGroupListJsonFormat = jsonFormat1(UserGroupList)

  // Message exception
  implicit val parseMessageExceptionJsonFormat = jsonFormat1(SerializationMessageException)

}
