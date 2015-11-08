package yields.server

import com.redis.RedisClientPool
import yields.server.dbi.exceptions.{ModelValueNotSetException, RedisNotAvailableException}
import yields.server.utils.Config

import scala.language.implicitConversions

/**
  * All Redis database interface related values and functions.
  */
package object dbi {

  private[dbi] val redis = new RedisClientPool(
    host = Config.getString("database.addr"),
    port = Config.getInt("database.port"),
    secret = Some(Config.getString("database.pass")),
    database = Config.getInt("database.id")
  )

  /** Terminates database connection. */
  def close(): Unit = {
    redis.withClient(_.disconnect)
    redis.closegi
  }

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
