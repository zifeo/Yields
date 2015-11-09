package yields.server.actions

import yields.server.mpi.Metadata

/**
 * Every action happening in the pipeline.
 */
trait Action {

  /**
   * Run the action given the sender.
   * @param metadata action requester
   * @return action result
   */
  def run(metadata: Metadata): Result

}

