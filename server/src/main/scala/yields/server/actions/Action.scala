package yields.server.actions

import yields.server.models.UID

/**
 * Base class of every actions happening in the pipeline.
 */
trait Action {

  def run(sender: UID): Result

}

