package yields.server

import com.redis.RedisClientPool
import yields.server.dbi.exceptions.{ModelValueNotSetException, RedisNotAvailableException}
import yields.server.utils.Config

import scala.language.implicitConversions

/**
  *
  * users:uid Long
  * users:[uid] Map[attributes -> value]
  * users:indexes:mail Map[mail -> uid]
  *
  * nodes:nid Long
  * nodes:[nid] Map[attributes -> value]
  * nodes:[nid]:users List[UID]
  * nodes:[nid]:nodes List[NID]
  * nodes:[nid]:feed Zset[tid -> (uid, text, nid, datetime)]
  * nodes:indexes:live Zset[time -> uid]
  *
  */
package object dbi {

  private[dbi] val redis = new RedisClientPool(
    host = Config.getString("database.addr"),
    port = Config.getInt("database.port"),
    secret = Some(Config.getString("database.pass"))
  )

  /** Terminates database connection. */
  def close(): Unit = redis.close

  // Gets the value if set or else throws an exception (cannot be unset).
  private[dbi] def valueOrException[T](value: Option[T]): T =
    value.getOrElse(throw new ModelValueNotSetException)

  // Gets the value if set or else gets default value.
  private[dbi] def valueOrDefault[T](value: Option[T], default: T): T =
    value.getOrElse(default)

  // Returns whether the count is one or throws an exception on error.
  private[dbi] def hasChangeOneEntry(count: Option[Long]): Boolean =
    count.getOrElse(throw new RedisNotAvailableException) == 1

}
