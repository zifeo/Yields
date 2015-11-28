package yields.server

import spray.json.DefaultJsonProtocol._
import yields.server.actions.groups._
import yields.server.actions.nodes._
import yields.server.actions.users._
import yields.server.io.actions.{BroadcastJsonFormat, ActionJsonFormat, ResultJsonFormat}
import yields.server.io.models.{GroupJsonFormat, NodeJsonFormat, UserJsonFormat}
import yields.server.io.mpi.{NotificationJsonFormat, RequestJsonFormat, ResponseJsonFormat}
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

  /** *** Actions  *****/

  implicit lazy val actionJF = ActionJsonFormat
  implicit lazy val resultJF = ResultJsonFormat
  implicit lazy val broadcastJF = BroadcastJsonFormat

  // Groups

  implicit lazy val groupCreateJF = jsonFormat5(GroupCreate)
  implicit lazy val groupCreateResJF = jsonFormat1(GroupCreateRes)

  implicit lazy val groupUpdateJF = jsonFormat3(GroupUpdate)
  implicit lazy val groupUpdateResJF = jsonFormat0(GroupUpdateRes)

  implicit lazy val groupSearchJF = jsonFormat1(NodeSearch)
  implicit lazy val groupSearchResJF = jsonFormat1(NodeSearchRes)

  implicit lazy val groupManageJF = jsonFormat5(GroupManage)
  implicit lazy val groupManageResJF = jsonFormat0(GroupManageRes)

  // Nodes

  implicit lazy val nodeMessageJF = jsonFormat4(NodeMessage)
  implicit lazy val nodeMessageResJF = jsonFormat2(NodeMessageRes)

  implicit lazy val nodeHistoryJF = jsonFormat3(NodeHistory)
  implicit lazy val nodeHistoryResJF = jsonFormat6(NodeHistoryRes)

  // Users

  implicit lazy val userConnectJF = jsonFormat1(UserConnect)
  implicit lazy val userConnectResJF = jsonFormat2(UserConnectRes)

  implicit lazy val userGroupListJF = jsonFormat0(UserGroupList)
  implicit lazy val userGroupListResJF = jsonFormat3(UserGroupListRes)

  implicit lazy val userUpdateJF = jsonFormat5(UserUpdate)
  implicit lazy val userUpdateResJF = jsonFormat0(UserUpdateRes)
  implicit lazy val userUpdateBrdJF = jsonFormat4(UserUpdateBrd)

  implicit lazy val userInfoJF = jsonFormat1(UserInfo)
  implicit lazy val userInfoResJF = jsonFormat5(UserInfoRes)

  implicit lazy val userSearchJF = jsonFormat1(UserSearch)
  implicit lazy val userSearchResJF = jsonFormat1(UserSearchRes)

  /** *** Message passing interface *****/

  implicit lazy val requestJF = RequestJsonFormat
  implicit lazy val responseJF = ResponseJsonFormat
  implicit lazy val notificationJF = NotificationJsonFormat
  implicit lazy val metadataJF = jsonFormat3(Metadata.apply)

}
