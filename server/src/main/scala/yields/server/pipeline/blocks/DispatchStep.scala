package yields.server.pipeline.blocks

import akka.event.LoggingAdapter
import akka.stream.Supervision
import akka.stream.stage._
import yields.server.actions.BroadcastResult
import yields.server.mpi.{Metadata, Response}

/**
  * Dispatch step takes care of handling [[BroadcastResult]].
  * Its sends to all of the receiver if a corresponding TCP socket flow is found.
  */
class DispatchStep(logger: LoggingAdapter) extends StatefulStage[Response, Response] {

  override def initial: StageState[Response, Response] = new StageState {

    override def onPush(elem: Response, ctx: Context[Response]): SyncDirective = elem match {
      case current @ Response(broadcast: BroadcastResult, metadata) =>
        val results = broadcast.receivers.map(uid => Response(broadcast, Metadata(uid)))
        emit((current +: results).iterator, ctx)
      case _ =>
        ctx.push(elem)
    }

  }

  override def onUpstreamFinish(ctx: Context[Response]): TerminationDirective = super.onUpstreamFinish(ctx)

  override def preStart(ctx: LifecycleContext): Unit = super.preStart(ctx)

  override def onDownstreamFinish(ctx: Context[Response]): TerminationDirective = super.onDownstreamFinish(ctx)

  override def onUpstreamFailure(cause: Throwable, ctx: Context[Response]): TerminationDirective =
    super.onUpstreamFailure(cause, ctx)

  override def postStop(): Unit = super.postStop()

  override def decide(t: Throwable): Supervision.Directive = super.decide(t)

  override def restart(): Stage[Response, Response] = super.restart()

}

/** [[DispatchStep]] companion. */
object DispatchStep {

  /** Creates a new dispatch step */
  def apply()(implicit logger: LoggingAdapter): DispatchStep = new DispatchStep(logger)

}