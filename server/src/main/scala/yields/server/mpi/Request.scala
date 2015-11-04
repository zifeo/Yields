package yields.server.mpi

import yields.server.actions.Action

/**
 * Incoming message having metadata and an action from the network.
 * @param action requested action
 * @param metadata common metadata
 */
case class Request(action: Action, metadata: Metadata)