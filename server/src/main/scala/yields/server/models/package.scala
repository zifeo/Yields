package yields.server

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

  /** Represents an email address. */
  type Email = String

  /** Represents a byte array. */
  type Blob = Array[Byte]

}
