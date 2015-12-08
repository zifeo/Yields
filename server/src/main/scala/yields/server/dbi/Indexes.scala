package yields.server.dbi

import yields.server.dbi.models.{NID, UID}
import yields.server.utils.Config
import com.redis.serialization.Parse.Implicits._

import scala.collection.mutable

/**
  * Regroup all indexes.
  *
  * users:indexes:email Map[String, UID] - email -> user id
  * nodes:searchable:[name] Set[NID] - node ids
  */
private[dbi] object Indexes {

  val fuzycount = Config.getInt("database.fuzycount")

  object Key {
    val usersEmails = "users:indexes:email"
    val searchable = "nodes:searchable"
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
    valueOrException(redis(_.sadd(s"$base:$name", nid))) == 1
  }

  /**
    * Fuzy lookup if any name has some corresponding matches and gather them if some.
    * @param name fuzy lookup name
    * @return set of all matching nid
    */
  def searchableFuzyLookup(name: String): Set[NID] = {
    val base = Key.searchable
    val lookup = redis[Option[(Option[Int], Option[List[Option[String]]])]](_.scan[String](0, s"$base:*$name*", fuzycount))

    val keys = lookup match {
      case Some((_, Some(matches))) => matches.flatten
      case _ => List.empty
    }

    val fetched = redisPipeline[Option[Set[Some[NID]]]](r => keys.map(r.smembers[NID](_)))
    val res = mutable.Set.empty[NID]
    for {
      result <- fetched
      setOpt <- result
      set <- setOpt
      nidOpt <- set
      nid <- nidOpt
    } res += nid
    res.toSet
  }

}
