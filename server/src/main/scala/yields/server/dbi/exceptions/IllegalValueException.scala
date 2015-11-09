package yields.server.dbi.exceptions

/**
  * Thrown whenever a value should be set or settable.
  * @param message precise error
  */
class IllegalValueException(message: String) extends Exception(message)
