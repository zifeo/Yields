package yields.server.mpi

import yields.server.actions.Result

/**
  * Outgoing message having metadata and a result of an action to the network.
  * @param result action result
  * @param metadata common metadata
  */
case class Response(result: Result, metadata: Metadata)