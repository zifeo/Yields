package yields.server.dbi

import java.time.OffsetDateTime

/**
 * Provisory models types.
 */
package object models {

  private type ID = Long

  /** Represents an user id. */
  type UID = ID

  /** Represents a group id. */
  type GID = ID

  /** Represents a node id. */
  type NID = ID

  /** Represent an item of group content */
  type TID = ID

  /** Represents an email address. */
  type Email = String

  /** Represents a byte array. */
  type Blob = String

  /** Represents a node. */
  // type Node = Int

  /** */
  type FeedContent = (UID, String, NID, OffsetDateTime)

}
