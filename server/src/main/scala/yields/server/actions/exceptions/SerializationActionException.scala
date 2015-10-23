package yields.server.actions.exceptions

/**
 * Represents a message unable to be (un)serialized by the [[yields.server.pipeline.ParserModule]].
 * @param message description
 */
case class SerializationActionException(message: String) extends ActionException