package yields.server.actions

import yields.server.dbi.models.UID

/**
  * Every result aimed to more than on user yield by an action in the pipeline.
  * A broadcast result does not guarantee the receivers will get the message.
  * The receiver must own a current and active TCP socket connection.
  */
trait BroadcastResult extends Result {

  val receivers: Seq[UID]

}
