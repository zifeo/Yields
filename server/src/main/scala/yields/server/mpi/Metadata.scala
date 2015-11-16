package yields.server.mpi

import java.time.OffsetDateTime

import yields.server.dbi.models.UID
import yields.server.utils.{Temporal, Config}

/**
 * Encapsulate sender, time and security data to allow independent checking from running.
 * @param sender message emitter
 * @param datetime sending time
 */
case class Metadata(sender: UID, receiver: UID, datetime: OffsetDateTime) {

  /** Creates new metadata for replying to sender UID from the server. */
  def reply: Metadata = {
    assert(receiver == Metadata.serverUID, "request receiver must be the server")
    copy(sender = Metadata.serverUID, receiver = sender, datetime = Temporal.current)
  }

}

/** [[Metadata]] companion. */
object Metadata {

  private val serverUID = Config.getLong("serverUID")

  /** Creates new metadata for sending to a UID from the server. */
  def to(uid: UID): Metadata =
    Metadata(serverUID, uid, Temporal.current)

}