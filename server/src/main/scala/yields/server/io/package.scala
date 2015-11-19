package yields.server

import spray.json.DefaultJsonProtocol._
import yields.server.actions.groups._
import yields.server.actions.images._
import yields.server.actions.nodes._
import yields.server.actions.users._
import yields.server.io.actions.{ActionJsonFormat, ResultJsonFormat}
import yields.server.io.models.{ImageJsonFormat, GroupJsonFormat, NodeJsonFormat, UserJsonFormat}
import yields.server.io.mpi.{RequestJsonFormat, ResponseJsonFormat}
import yields.server.mpi.Metadata

/**
  * Provides json format for read/write specification.
  */
package object io {

  implicit lazy val offsetDateTimeJF = OffsetDateTimeJsonFormat

  /** *** Models *****/

  implicit lazy val nodeJF = NodeJsonFormat
  implicit lazy val groupJF = GroupJsonFormat
  implicit lazy val userJF = UserJsonFormat
  implicit lazy val imageJF = ImageJsonFormat

  /** *** Actions  *****/

  implicit lazy val actionJF = ActionJsonFormat
  implicit lazy val resultJF = ResultJsonFormat

  // Groups

  implicit lazy val groupCreateJF = jsonFormat3(GroupCreate)
  implicit lazy val groupCreateResJF = jsonFormat1(GroupCreateRes)

  implicit lazy val groupUpdateJF = jsonFormat3(GroupUpdate)
  implicit lazy val groupUpdateResJF = jsonFormat0(GroupUpdateRes)

  implicit lazy val groupMessageJF = jsonFormat2(GroupMessage)
  implicit lazy val groupMessageResJF = jsonFormat1(GroupMessageRes)

  implicit lazy val groupHistoryJF = jsonFormat3(GroupHistory)
  implicit lazy val groupHistoryResJF = jsonFormat2(GroupHistoryRes)

  implicit lazy val groupSearchJF = jsonFormat1(GroupSearch)
  implicit lazy val groupSearchResJF = jsonFormat1(GroupSearchRes)

  // Users

  implicit lazy val userConnectJF = jsonFormat1(UserConnect)
  implicit lazy val userConnectResJF = jsonFormat1(UserConnectRes)

  implicit lazy val userGroupListJF = jsonFormat1(UserGroupList)
  implicit lazy val userGroupListResJF = jsonFormat1(UserGroupListRes)

  implicit lazy val userUpdateJF = jsonFormat4(UserUpdate)
  implicit lazy val userUpdateResJF = jsonFormat0(UserUpdateRes)

  implicit lazy val userInfoJF = jsonFormat1(UserInfo)
  implicit lazy val userInfoResJF = jsonFormat3(UserInfoRes)

  // Images

  implicit lazy val imageGetJF = jsonFormat1(ImageGet)
  implicit lazy val imageGetResJF = jsonFormat1(ImageGetRes)

  implicit lazy val imageSetJF = jsonFormat1(ImageSet)
  implicit lazy val imageSetResJF = jsonFormat1(ImageSetRes)


  /** *** Message passing interface *****/

  implicit lazy val requestJF = RequestJsonFormat
  implicit lazy val responseJF = ResponseJsonFormat
  implicit lazy val metadataJF = jsonFormat2(Metadata)

}
