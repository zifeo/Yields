package yields.server.actions.exceptions

/**
  * Thrown whenever action or action effect is not allowed.
  * @param message precise error
  */
class UnauthorizedActionException(message: String) extends Exception(message)
