package yields.server.mpi.exceptions

import yields.server.mpi.Message

/**
 * Base class of every exception related to a message and happening in the pipeline.
 */
trait MessageException extends Exception with Message { // TODO : extending Exception really needed?

  /** Description. */
  val message: String

}
