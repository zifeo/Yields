package yields.server

import com.redis.{RedisClient, RedisClientPool}
import yields.server.dbi.exceptions.IllegalValueException
import yields.server.utils.{Temporal, Config}

import scala.language.implicitConversions

/**
  * All Redis database interface related values and functions.
  *
  * Redis scheme:
  * identity Long - last user id created
  */
package object dbi {

  object Key {
    val identity = "identity"
  }

  private[dbi] lazy val redis = {
    val pool = new RedisClientPool(
      host = Config.getString("database.addr"),
      port = Config.getInt("database.port"),
      secret = Some(Config.getString("database.pass"))
    )
    /* TODO: upgrade to newest scala redis client where selecting
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

  /**
    * Gets new global identifier.
    * @return identity as a number
    */
  private[dbi] def newIdentity(): Long = {
    valueOrException(redis.withClient(_.incr(Key.identity)))
  }

  /**
    * Gets the value if set or else throws an exception (cannot be unset).
    * @param value option value
    * @tparam T type of value
    * @throws IllegalValueException if value not found
    * @return value
    */
  private[dbi] def valueOrException[T](value: Option[T]): T =
    value.getOrElse(throw new IllegalValueException("value should be set"))

  /**
    * Gets the value if set or else gets default value.
    * @param value option value
    * @param default fallback value
    * @tparam T type of value
    * @return value or default if not found
    */
  private[dbi] def valueOrDefault[T](value: Option[T], default: T): T =
    value.getOrElse(default)

  /**
    * Add a value to a redis zset.
    * @param key zset key
    * @param element value to add
    * @tparam T type of the value
    * @return true if addition done
    */
  private[dbi] def zaddWithTime[T](key: String, element: T): Boolean =
    valueOrException(redis(_.zadd(key, Temporal.now.toEpochSecond, element))) == 1

  /**
    * Add values to a redis zset.
    * @param key zset key
    * @param elements values to add
    * @tparam T type of values
    * @throws IllegalArgumentException if no element given
    * @return true if all additions were done
    */
  private[dbi] def zaddWithTime[T](key: String, elements: List[T]): Boolean = elements match {
    case Nil => throw new IllegalArgumentException("elements cannot be empty")
    case e :: Nil => zaddWithTime(key, e)
    case e :: es =>
      val now = Temporal.now.toEpochSecond.toDouble
      val others = es.zipWithIndex.map { case (u, i) =>
        now + 1 + i -> u
      }
      valueOrException(redis(_.zadd(key, now, e, others: _*))) == elements.size
  }

  /**
    * Remove a value from a redis zset.
    * @param key zset key
    * @param element value to be removed
    * @tparam T type of value
    * @return true if removing done
    */
  private[dbi] def zremWithTime[T](key: String, element: T): Boolean =
    valueOrException(redis(_.zrem(key, element))) == 1

  /**
    * Remove values from a redis zset.
    * @param key zset key
    * @param elements values to be removed
    * @tparam T types of values
    * @throws IllegalArgumentException if no element given
    * @return true if all removings were done
    */
  private[dbi] def zremWithTime[T](key: String, elements: List[T]): Boolean = elements match {
    case Nil => throw new IllegalArgumentException("elements cannot be empty")
    case e :: Nil => zremWithTime(key, e)
    case e :: es => valueOrException(redis(_.zrem(key, e, es: _*))) == elements.size
  }

}
