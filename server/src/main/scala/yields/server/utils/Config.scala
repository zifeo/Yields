package yields.server.utils

import com.typesafe.config.ConfigFactory

/** Yields configuration accessor. */
object Config {

  private[utils] lazy val config = ConfigFactory.load().getConfig("yields")

  /** Get a String from the key. */
  def getString(e: String): String = config.getString(e)

  /** Get an Int from the key. */
  def getInt(e: String): Int = config.getInt(e)

}
