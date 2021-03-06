package yields.server.dbi

import yields.server.dbi.models.{NID, UID}
import yields.server.utils.Config
import com.redis.serialization.Parse.Implicits._

/**
  * Regroup all indexes.
  *
  * users:indexes:email Map[String, UID] - email -> user id
  * nodes:searchable:[name] Set[NID] - node ids
  * nodes:rss Set[NID] - rss ids
  */
private[dbi] object Indexes {

  val fuzycount = Config.getInt("database.fuzycount")

  object Key {
    val usersEmails = "users:indexes:email"
    val searchable = "nodes:searchable"
    val rss = "nodes:rss"
  }

  /**
    * Register a given email and user in the index.
    * @param email key email
    * @param uid value user id
    * @return true on success
    */
  def userEmailRegister(email: String, uid: UID): Boolean =
    redis(_.hset(Key.usersEmails, email, uid))

  /**
    * Lookup if given email has a corresponding match and returns it if found.
    * @param email lookup email
    * @return corresponding user id if any
    */
  def userEmailLookup(email: String): Option[UID] =
    redis(_.hget[UID](Key.usersEmails, email))

  /**
    * Register a given name and nid in the index.
    * @param name key name
    * @param nid value node id
    * @return true on success
    */
  def searchableRegister(name: String, nid: NID): Boolean = {
    val base = Key.searchable
    valueOrException(redis(_.sadd(s"$base:$name".toLowerCase, nid))) == 1
  }

  /**
    * Unregister a given name and nid in the index.
    * @param name key name
    * @param nid value node id
    * @return true on success
    */
  def searchableUnregister(name: String, nid: NID): Boolean = {
    val base = Key.searchable
    valueOrException(redis(_.srem(s"$base:$name".toLowerCase, nid))) == 1
  }

  /**
    * Fuzy lookup if any name has some corresponding matches and gather them if some.
    * @param name fuzy lookup name
    * @return set of all matching nid
    */
  def searchableFuzyLookup(name: String): Set[NID] = {
    val base = Key.searchable
    val lookup = redis[Option[(Option[Int], Option[List[Option[String]]])]] (_.scan[String](
      0,
      s"$base:*$name*".toLowerCase,
      Integer.MAX_VALUE
    ))

    val keys = lookup match {
      case Some((_, Some(matches))) => matches.flatten.take(fuzycount)
      case _ => List.empty
    }

    val fetched = redisPipeline[Option[Set[Option[NID]]]](r => keys.map(r.smembers[NID](_)))
    fetched.toList.flatten.flatten.flatten.flatten.toSet
  }

  /**
    * Register a given rss.
    * @param nid rss id
    * @return true on success
    */
  def rssRegister(nid: NID): Boolean =
    valueOrException(redis(_.sadd(Key.rss, nid))) == 1

  /**
    * Lookup returning all rss.
    * @return all rss
    */
  def rssLookup: List[NID] =
    valueOrException(redis(_.smembers[NID](Key.rss))).toList.flatten

}
