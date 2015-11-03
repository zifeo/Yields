package yields.server.dbi.models

import yields.server.dbi._
import yields.server.utils.Helpers

/**
 *
 * nodes:nid Long
 * nodes:[nid] Map[attributes -> value]
 * nodes:[nid]:users List[UID]
 * nodes:[nid]:nodes List[NID]
 * nodes:[nid]:feed Zset[tid -> (uid, text, nid, datetime)]
 */

/**
 * Representation of a group
 */
final class Group {
  var id: String = _
  var version: String = _
  var group_name: String = _
  var date_creation: java.util.Date = _
  var last_activity: java.util.Date = _
  var messages: java.util.List[String] = new java.util.ArrayList()

  override def toString = s"Group: $id, name: $group_name , creation: $date_creation, last activity: $last_activity"

}

object Group {

//  def createGroup(name: String): GID = {
//
//    val struct = Map(
//      "datetime" -> Helpers.currentDatetime.toString,
//      "name" -> name
//    )
//
//    val gid = r.incr("ids:last:groups") match {
//      case Some(id) => id
//      case None => throw new Exception
//    }
//
//    r.hmset(s"groups:$gid", struct)
//    r.set(s"groups:$gid:nid", "0")
//
//    gid
//  }
//
//  //  def getUsersFromGroup(ridGroup: String): List[User] = {
//  //    queryBySql("select from E where in = ?", new ORecordId(ridGroup))
//  //  }
//  //
//  def addMessage(ridGroup: String, ridSender: String, body: String) = {
//
//    val nid = r.incr(s"groups:$ridGroup:nid") match {
//      case Some(id) => id
//      case None => throw new Exception
//    }
//
//    val struct = Map(
//      "sender" -> ridSender,
//      "datetime" -> Helpers.currentDatetime,
//      "content" -> body
//    )
//
//    r.hmset(s"groups:$ridGroup:content:$nid", struct)
//
//    nid
//
//  }
//
//  def getLast(ridGroup: String, last: Long, count: Long) = {
//
//    val lastNid = r.get(s"groups:$ridGroup:nid") match {
//      case Some(id) => id
//      case None => throw new Exception
//    }
//
//    val from = Math.max(0, lastNid.toInt - count) to lastNid.toInt
//
//    val result = from.map { nid =>
//
//      r.hmget(s"groups:$ridGroup:content:$nid", "datetime", "sender", "content")
//
//    }.flatten
//
//    println(result)
//
//  }
//  //
//  //  def getGroupInfos(ridGroup: String): Group = {
//  //    val res = queryBySql("select from ?", new ORecordId(ridGroup))
//  //    res match {
//  //      case Nil => throw new NoSuchElementException
//  //      case head :: tail => res.head
//  //    }
//  //  }

}