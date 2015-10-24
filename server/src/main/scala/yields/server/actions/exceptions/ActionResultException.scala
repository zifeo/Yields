package yields.server.actions.exceptions

import yields.server.actions.{Result, Action}
import yields.server.dbi.models.UID

/**
 * Base class of every exception related to an action and happening in the pipeline.
 */
trait ActionResultException extends Exception with Action with Result {

  /** Description. */
  val message: String

  /** An exception stays an exception as it is run. */
  final def run(ignored: UID): Result = this

}
