package yields.server

import com.redis.RedisClient
import yields.server.dbi.exceptions.{RedisNotAvailableException, ModelValueNotSetException}
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

  private[dbi] lazy val redis = {
    val re = new RedisClient(Config.getString("database.addr"), Config.getInt("database.port"))
    if (! re.auth(Config.getString("database.pass"))) {
      throw new Error
    }
    re
  }

  /** Terminates database connection. */
  def closeDatabase(): Unit = redis.quit

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
