package yields.server

import com.redis.RedisClient
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

  private [dbi] lazy val redis = {
    val re = new RedisClient(Config.getString("database.addr"), Config.getInt("database.port"))
    if (! re.auth(Config.getString("database.pass"))) {
      throw new Error
    }
    re.setnx("users:uid", 0)
    re.setnx("nodes:nid", 0)
    re
  }

  /** Terminates database connection. */
  def closeDatabase(): Unit = redis.quit

}
