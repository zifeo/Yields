package yields.server

import spray.json.DefaultJsonProtocol._
import yields.server.actions.exceptions.SerializationException
import yields.server.actions.groups._
import yields.server.actions.users._
import yields.server.io.actions.{ActionResultExceptionJsonFormat, ActionJsonFormat}
import yields.server.io.models.{UserJsonFormat, GroupJsonFormat}
import yields.server.io.mpi.{RequestJsonFormat, ResponseJsonFormat}
import yields.server.mpi.Metadata

/**
 * Provides json format for read/write specification.
 */
package object io {

  implicit lazy val offsetDateTimeJF = OffsetDateTimeJsonFormat

  /***** Models *****/

  implicit lazy val groupJF = GroupJsonFormat
  implicit lazy val userJF = UserJsonFormat

  /***** Actions  *****/

  implicit lazy val actionJF = ActionJsonFormat

  // Exceptions

  implicit lazy val actionResultExceptionJF = ActionResultExceptionJsonFormat
  implicit val serializationExceptionJF = jsonFormat1(SerializationException)

  // Groups

  implicit lazy val groupCreateJF = jsonFormat2(GroupCreate)
  implicit lazy val groupCreateResJF = jsonFormat1(GroupCreateRes)

  implicit lazy val groupUpdateJF = jsonFormat3(GroupUpdate)
  implicit lazy val groupUpdateResJF = jsonFormat0(GroupUpdateRes)

  implicit lazy val groupMessageJF = jsonFormat2(GroupMessage)
  implicit lazy val groupMessageResJF = jsonFormat0(GroupMessageRes)

  implicit lazy val groupHistoryJF = jsonFormat3(GroupHistory)
  implicit lazy val groupHistoryResJF = jsonFormat1(GroupHistoryRes)

  // Users

  implicit lazy val userConnectJF = jsonFormat1(UserConnect)
  implicit lazy val userConnectResJF = jsonFormat1(UserConnectRes)

  implicit lazy val userGroupListJF = jsonFormat1(UserGroupList)
  implicit lazy val userGroupListResJF = jsonFormat1(UserGroupListRes)

  implicit lazy val userUpdateJF = jsonFormat4(UserUpdate)
  implicit lazy val userUpdateResJF = jsonFormat0(UserUpdateRes)

  /***** Message passing interface *****/

  implicit lazy val requestJS = RequestJsonFormat
  implicit lazy val responseJS = ResponseJsonFormat
  implicit lazy val metadataJS = jsonFormat2(Metadata)


}
