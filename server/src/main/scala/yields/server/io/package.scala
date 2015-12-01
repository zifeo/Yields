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

  implicit lazy val groupCreateJF = jsonFormat3(GroupCreate)
  implicit lazy val groupCreateResJF = jsonFormat1(GroupCreateRes)
  implicit lazy val groupCreateBrdJF = jsonFormat4(GroupCreateBrd)

  implicit lazy val groupUpdateJF = jsonFormat7(GroupUpdate)
  implicit lazy val groupUpdateResJF = jsonFormat0(GroupUpdateRes)
  implicit lazy val groupUpdateBrdF = jsonFormat5(GroupUpdateBrd)

  implicit lazy val groupInfoJF = jsonFormat1(GroupInfo)
  implicit lazy val groupInfoResJF = jsonFormat5(GroupInfoRes)

  implicit lazy val groupMessageJF = jsonFormat4(GroupMessage)
  implicit lazy val groupMessageResJF = jsonFormat2(GroupMessageRes)
  implicit lazy val groupMessageBrdJF = jsonFormat6(GroupMessageBrd)

  // Nodes

  implicit lazy val groupSearchJF = jsonFormat1(NodeSearch)
  implicit lazy val groupSearchResJF = jsonFormat3(NodeSearchRes)

  implicit lazy val nodeHistoryJF = jsonFormat3(NodeHistory)
  implicit lazy val nodeHistoryResJF = jsonFormat6(NodeHistoryRes)

  // Users

  implicit lazy val userConnectJF = jsonFormat1(UserConnect)
  implicit lazy val userConnectResJF = jsonFormat2(UserConnectRes)

  implicit lazy val userGroupListJF = jsonFormat0(UserNodeList)
  implicit lazy val userGroupListResJF = jsonFormat3(UserNodeListRes)

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
