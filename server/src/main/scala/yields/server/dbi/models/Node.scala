package yields.server.dbi.models

import yields.server.dbi._
import yields.server.dbi.exceptions.RedisNotAvailableException
import yields.server.utils.Helpers
import com.redis.serialization._
import Parse.Implicits._

/**
 *
 * nodes:nid Long
 * nodes:[nid] Map[attributes -> value]   containing date_creation, last_activity, name, kind
 * nodes:[nid]:users Zset[UID]
 * nodes:[nid]:nodes Zset[NID]
 * nodes:[nid]:feed Zset[tid -> (uid, text, nid, datetime)]
 * nodes:[nid]:content Binary
 * nodes:nid -> last used nid
 */

/**
 * Representation of a node
 */
class Node(val nid: NID) {

  object Key {
    val infos = s"nodes:$nid"
    val users = s"nodes:$nid:users"
    val nodes = s"nodes:$nid:nodes"
    val feed = s"nodes:$nid:feed"
    val tid = s"nodes:$nid:tid"
  }

  type feedEntry = Map[TID, feedContent]

  var _infos: Option[Map[String, String]] = None
  var _users: Option[List[UID]] = None
  var _nodes: Option[List[NID]] = None
  var _feed: Option[List[feedEntry]] = None


  /** infos getter */
  def infos: Map[String, String] = _infos.getOrElse {
    _infos = redis.hmget(Key.infos)
    valueOrDefault(_infos, Map())
  }

  /** infos setter */
  def infos_(newInfos: Map[String, String]): Unit =
    _infos = update(Key.infos, newInfos)

  /** users getter */
  def users: List[UID] = _users.getOrElse {
    _users = redis.zrange[UID](Key.users, 0, -1)
    valueOrDefault(_users, List())
  }

  /** add user */
  def addUser(id: UID): Boolean =
    hasChangeOneEntry(redis.zadd(Key.users, Helpers.currentDatetime.toEpochSecond, id))

  /** get n messages from an index */
  def getMessagesInRange(start: Int, n: Int): List[feedEntry] = {
    _feed = redis.zrange[feedEntry](Key.feed, start, n)
    valueOrDefault(_feed, List())
  }

  /** add message */
  def addMessage(content: feedContent): Boolean = {
    val tid: TID = redis.incr(Key.tid).getOrElse(0)
    val entry: feedEntry = Map(tid -> content)
    hasChangeOneEntry(redis.zadd(Key.feed, Helpers.currentDatetime.toEpochSecond, entry))
  }

  private def valueOrDefault[T](value: Option[T], default: T): T =
    value.getOrElse(default)

  // Updates the field with given value and actualize timestamp.
  private def update[T](field: String, value: T): Option[T] = {
    val status = redis.hmset(field, List((field, value)))
    if (!status) throw new RedisNotAvailableException
    Some(value)
  }

  // Returns whether the count is one or throws an exception on error.
  private def hasChangeOneEntry(count: Option[Long]): Boolean =
    1 == count.getOrElse(throw new RedisNotAvailableException)
}

object Node {


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

}