package yields.server.mpi

import java.time.OffsetDateTime

import yields.server.dbi.models.UID
import yields.server.utils.{Temporal, Config}

/**
  * Encapsulate sender, time and security data to allow independent checking from running.
  * @param client message emitter
  * @param datetime sending time
  * @param ref client-side reference
  */
case class Metadata(client: UID, datetime: OffsetDateTime, ref: OffsetDateTime) {

  /** Creates new metadata for replying from the server. */
  def replied: Metadata =
    copy(datetime = Temporal.now)

}

/** [[Metadata]] companion. */
object Metadata {

  /** Creates new metadata for sending to a UID from the server. */
  def now(uid: UID): Metadata =
    Metadata(uid, Temporal.now, Temporal.minimum)

}