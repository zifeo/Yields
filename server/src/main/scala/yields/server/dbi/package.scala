package yields.server

import scala.language.implicitConversions

package object dbi {

  /** Terminates database connection. */
  def closeDatabase(): Unit = ()

}
