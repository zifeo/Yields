package yields.server.mpi

import java.time.OffsetDateTime

import yields.server.models.UID

/**
 * Encapsulate sender, time and security data to allow independent checking from running.
 * @param sender message emitter
 * @param datetime sending time
 */
case class Metadata(sender: UID, datetime: OffsetDateTime)