package yields.server.actions

import yields.server.dbi.models.UID

/**
 * Every action happening in the pipeline.
 */
trait Action {

  /**
   * Run the action given the sender.
   * @param sender action requester
   * @return action result
   */
  def run(sender: UID): Result

}

