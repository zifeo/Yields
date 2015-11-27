package yields.server.mpi

import yields.server.actions.Broadcast

/**
  * Outgoing message having metadata and a notification of an action.
  * @param bcast action broadcast
  * @param metadata common metadata
  */
case class Notification(bcast: Broadcast, metadata: Metadata)