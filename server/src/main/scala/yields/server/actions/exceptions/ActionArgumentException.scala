package yields.server.actions.exceptions

/**
  * Thrown whenever actions arguments are invalid.
  * @param message precise error
  */
class ActionArgumentException(message: String) extends Exception(message)
