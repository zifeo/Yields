package yields.server.utils

import com.typesafe.config.ConfigFactory

/** Yields configuration adapter accessor. */
object Config {

  private lazy val config = ConfigFactory.load().getConfig("yields")

  /** Gets a String from the key. */
  def getString(e: String): String = config.getString(e)

  /** Gets an Int from the key. */
  def getInt(e: String): Int = config.getInt(e)

}
