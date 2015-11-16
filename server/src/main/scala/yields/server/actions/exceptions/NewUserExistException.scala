package yields.server.actions.exceptions

/** Error used to detect an attempt to create a new user but with an email that already exists */
class NewUserExistException(message: String) extends Exception(message)