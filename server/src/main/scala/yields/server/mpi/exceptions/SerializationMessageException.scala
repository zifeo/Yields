package yields.server.mpi.exceptions

/**
 * Represents a message unable to be (un)serialized by the [[yields.server.pipeline.ParserModule]].
 * @param message description
 */
case class SerializationMessageException(message: String) extends MessageException