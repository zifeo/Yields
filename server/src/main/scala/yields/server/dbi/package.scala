package yields.server

import com.redis.RedisClient
import yields.server.utils.Config

import scala.language.implicitConversions

package object dbi {

  val r = new RedisClient(Config.getString("database.addr"), Config.getInt("database.port"))
  r.auth(Config.getString("database.pass"))

  r.setnx("ids:last:groups", 0)
  r.setnx("ids:last:users", 0)
  r.setnx("ids:last:nodes", 0)

  /** Terminates database connection. */
  def closeDatabase(): Unit = r.quit

}
