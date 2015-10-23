package yields.server.actions.exceptions

/**
 * Represents an action (de)serializable by the [[yields.server.pipeline.SerializationModule]].
 * @param message description
 */
case class SerializationException(message: String) extends ActionResultException