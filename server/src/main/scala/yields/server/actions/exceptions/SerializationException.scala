package yields.server.actions.exceptions

import yields.server.pipeline.modules.SerializationModule

/**
 * Represents an action (de)serializable by the [[SerializationModule]].
 * @param message description
 */
case class SerializationException(message: String) extends ActionResultException