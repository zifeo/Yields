package yields.server.pipeline.blocks

import akka.stream.scaladsl.BidiFlow

/**
  * Wraps [[BidiFlow]] for clearer creation.
  *
  * {{{
  *            module
  *         +----------+
  *  In1 ~> | incoming | ~> Out1
  *         |          |
  * Out2 <~ | outgoing | <~ In2
  *         +----------+
  * }}}
  *
  * @tparam In1 incoming argument
  * @tparam Out1 incoming result
  * @tparam In2 outgoing argument
  * @tparam Out2 outgoing result
  */
trait Module[-In1, +Out1, -In2, +Out2] {

  /**
    * Incoming flow transformation or effect.
    */
  val incoming: In1 => Out1
  /**
    * Outgoing flow transformation or effect.
    */
  val outgoing: In2 => Out2

  /**
    * Builds the [[BidiFlow]].
    * @return bidirectional module
    */
  def create: BidiFlow[In1, Out1, In2, Out2, Unit] =
    BidiFlow.fromFunctions(incoming, outgoing)

}
