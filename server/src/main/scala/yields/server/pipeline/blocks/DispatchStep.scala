package yields.server.pipeline.blocks

import akka.event.LoggingAdapter
import akka.stream.Supervision
import akka.stream.stage._
import yields.server.actions.BroadcastResult
import yields.server.dbi.models.UID
import yields.server.mpi.{Metadata, Response}

/**
  * Dispatch step takes care of handling [[BroadcastResult]].
  * Its sends to all of the receiver if a corresponding TCP socket flow is found.
  */
class DispatchStep(logger: LoggingAdapter) extends StatefulStage[Response, Response] {

  override def initial: StageState[Response, Response] = new PushPoolState(Map.empty)

  override def onUpstreamFinish(ctx: Context[Response]): TerminationDirective = {
    logger.warning("onUpstreamFinish")
    super.onUpstreamFinish(ctx)
  }

  override def preStart(ctx: LifecycleContext): Unit = {
    logger.warning("preStart")
    super.preStart(ctx)
  }

  override def onDownstreamFinish(ctx: Context[Response]): TerminationDirective = {
    logger.warning("onDownstreamFinish")
    super.onDownstreamFinish(ctx)
  }

  override def onUpstreamFailure(cause: Throwable, ctx: Context[Response]): TerminationDirective = {
    logger.warning("onUpstreamFailure")
    super.onUpstreamFailure(cause, ctx)
  }

  override def postStop(): Unit = {
    logger.warning("postStop")
    super.postStop()
  }

  override def decide(t: Throwable): Supervision.Directive = {
    logger.warning("decide")
    super.decide(t)
  }

  override def restart(): Stage[Response, Response] = {
    logger.warning("restart")
    super.restart()
  }

  private class PushPoolState(private val pool: Map[UID, Context[Response]]) extends StageState[Response, Response] {

    override def onPush(elem: Response, ctx: Context[Response]): SyncDirective = elem match {

      case current @ Response(broadcast: BroadcastResult, metadata) =>
        val sendList = for {
          receiver <- broadcast.receivers
          if pool.contains(receiver)
        } yield Response(broadcast, Metadata.now(receiver))
        emit(sendList.iterator, ctx)

      case _ =>
        ctx.push(elem)
    }

  }

}

/** [[DispatchStep]] companion. */
object DispatchStep {

  /** Creates a new dispatch step */
  def apply()(implicit logger: LoggingAdapter): DispatchStep = new DispatchStep(logger)

}