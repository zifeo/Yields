package yields.server.mpi

import yields.server.actions.Result

/**
 * Outgoing message having metadata and a result of an action to the network.
 * @param result action result (can be an exception)
 * @param metadata common metadata
 */
case class Response(result: Result, metadata: Metadata)