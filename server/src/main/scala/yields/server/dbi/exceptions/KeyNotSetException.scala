package yields.server.dbi.exceptions

/** Thrown when an attempt is made to query a key that is not set in the database */
class KeyNotSetException(message: String) extends Exception(message)