package yields.server.dbi.exceptions

/**
  * Thrown whenever an incrementation of an identifier fails.
  * @param message precise error
  */
class UnincrementableIdentifierException(message: String) extends Exception(message)