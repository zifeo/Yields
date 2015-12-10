package yields.server

import spray.json.DefaultJsonProtocol._
import yields.server.actions.groups._
import yields.server.actions.media.{MediaMessage, MediaMessageRes}
import yields.server.actions.nodes._
import yields.server.actions.publisher._
import yields.server.actions.rss.{RSSCreate, RSSCreateRes, RSSInfo, RSSInfoRes}
import yields.server.actions.users._
import yields.server.io.actions.{ActionJsonFormat, BroadcastJsonFormat, ResultJsonFormat}
import yields.server.io.mpi.{NotificationJsonFormat, RequestJsonFormat, ResponseJsonFormat}
import yields.server.mpi.Metadata

/**
  * Provides json format for read/write specification.
  */
package object io {

  implicit lazy val offsetDateTimeJF = OffsetDateTimeJsonFormat

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
  implicit lazy val groupMessageResJF = jsonFormat3(GroupMessageRes)

  // Nodes

  implicit lazy val nodeSearchJF = jsonFormat1(NodeSearch)
  implicit lazy val nodeSearchResJF = jsonFormat3(NodeSearchRes)

  implicit lazy val nodeInfoJF = jsonFormat1(NodeInfo)

  implicit lazy val nodeHistoryJF = jsonFormat3(NodeHistory)
  implicit lazy val nodeHistoryResJF = jsonFormat7(NodeHistoryRes)

  implicit lazy val nodeMessageBrdJF = jsonFormat7(NodeMessageBrd)

  // Users

  implicit lazy val userConnectJF = jsonFormat1(UserConnect)
  implicit lazy val userConnectResJF = jsonFormat2(UserConnectRes)

  implicit lazy val userGroupListJF = jsonFormat0(UserNodeList)
  implicit lazy val userGroupListResJF = jsonFormat4(UserNodeListRes)

  implicit lazy val userUpdateJF = jsonFormat5(UserUpdate)
  implicit lazy val userUpdateResJF = jsonFormat0(UserUpdateRes)
  implicit lazy val userUpdateBrdJF = jsonFormat4(UserUpdateBrd)

  implicit lazy val userInfoJF = jsonFormat1(UserInfo)
  implicit lazy val userInfoResJF = jsonFormat6(UserInfoRes)

  implicit lazy val userSearchJF = jsonFormat1(UserSearch)
  implicit lazy val userSearchResJF = jsonFormat1(UserSearchRes)

  // Publishers

  implicit lazy val publisherCreateJF = jsonFormat4(PublisherCreate)
  implicit lazy val publisherCreateResJF = jsonFormat1(PublisherCreateRes)
  implicit lazy val publisherCreateBrdJF = jsonFormat4(PublisherCreateBrd)

  implicit lazy val publisherUpdateJF = jsonFormat9(PublisherUpdate)
  implicit lazy val publisherUpdateResJF = jsonFormat0(PublisherUpdateRes)
  implicit lazy val publisherUpdateBrdF = jsonFormat5(PublisherUpdateBrd)

  implicit lazy val publisherInfoJF = jsonFormat1(PublisherInfo)
  implicit lazy val publisherInfoResJF = jsonFormat6(PublisherInfoRes)

  implicit lazy val publisherMessageJF = jsonFormat4(PublisherMessage)
  implicit lazy val publisherMessageResJF = jsonFormat3(PublisherMessageRes)

  // RSS

  implicit lazy val rssCreateJF = jsonFormat4(RSSCreate)
  implicit lazy val rssCreateResJF = jsonFormat1(RSSCreateRes)

  implicit lazy val rssInfoJF = jsonFormat1(RSSInfo)
  implicit lazy val rssInfoResJF = jsonFormat5(RSSInfoRes)

  // Media

  implicit lazy val mediaMessageJF = jsonFormat4(MediaMessage)
  implicit lazy val mediaMessageResJF = jsonFormat2(MediaMessageRes)

  /** *** Message passing interface *****/

  implicit lazy val requestJF = RequestJsonFormat
  implicit lazy val responseJF = ResponseJsonFormat
  implicit lazy val notificationJF = NotificationJsonFormat
  implicit lazy val metadataJF = jsonFormat3(Metadata.apply)

}
