package yields.server

import com.redis.{RedisClient, RedisClientPool}
import yields.server.dbi.exceptions.IllegalValueException
import yields.server.utils.Config

import scala.language.implicitConversions

/**
  * All Redis database interface related values and functions.
  */
package object dbi {

  private[dbi] lazy val redis = {
    val pool = new RedisClientPool(
      host = Config.getString("database.addr"),
      port = Config.getInt("database.port"),
      secret = Some(Config.getString("database.pass"))
    )
    /* TODO : upgrade to newest scala redis client where selecting
     * database when creating the pool is supported. Current version
     * has sadly the "select" query executed before "auth" one...
     */
    pool.withClient(_.select(Config.getInt("database.id")))
    pool
  }

  /**
    * Public accessor to database via local redis object.
    * @param query query to be run on redis
    * @tparam T return type of the query
    * @return values or status of redis query
    */
  def redis[T](query: RedisClient => T): T =
    redis.withClient(query)

  /**
    * Public accessor to pipelined database via local redis object.
    * @param queries queries to be run on in block
    * @return list of values or status of redis queries
    */
  def redisPipeline[T](queries: RedisClient#PipelineClient => Any): Option[List[Any]] =
    redis.withClient(_.pipeline(queries))

  /** Terminates database connection. */
  def close(): Unit = {
    redis.withClient(_.disconnect)
    redis.close
  }

  // Gets the value if set or else throws an exception (cannot be unset).
  private[dbi] def valueOrException[T](value: Option[T]): T =
    value.getOrElse(throw new IllegalValueException("value should be set"))

  // Gets the value if set or else gets default value.
  private[dbi] def valueOrDefault[T](value: Option[T], default: T): T =
    value.getOrElse(default)

  // Returns whether the count is one or throws an exception on error.
  private[dbi] def hasChangeOneEntry(count: Option[Long]): Boolean =
    valueOrException(count) == 1

}
