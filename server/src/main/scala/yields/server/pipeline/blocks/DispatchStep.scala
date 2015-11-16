package yields.server.pipeline.blocks

import akka.stream.Supervision
import akka.stream.stage._
import yields.server.actions.{BroadcastResult, Result}
import yields.server.mpi.{Metadata, Response}
import yields.server.utils.Temporal

/**
  * Dispatch step takes care of handling [[BroadcastResult]].
  * Its sends to all of the receiver if a corresponding TCP socket flow is found.
  */
class DispatchStep extends PushPullStage[Result, Response] {

  /** Returns current timed server metadata. */
  def currentServerMetadata(): Metadata =
    Metadata(7777, Temporal.current)

  override def onPush(elem: Result, ctx: Context[Response]): SyncDirective = {
    elem match {
      case broadcast: BroadcastResult =>
      case result: Result =>
    }

    ctx.push(Response(elem, currentServerMetadata()))
  }

  override def onPull(ctx: Context[Response]): SyncDirective = {
    ctx.pull()
  }

  override def preStart(ctx: LifecycleContext): Unit = super.preStart(ctx)

  override def onUpstreamFinish(ctx: Context[Response]): TerminationDirective = super.onUpstreamFinish(ctx)

  override def onDownstreamFinish(ctx: Context[Response]): TerminationDirective = super.onDownstreamFinish(ctx)

  override def onUpstreamFailure(cause: Throwable, ctx: Context[Response]): TerminationDirective =
    super.onUpstreamFailure(cause, ctx)

  override def postStop(): Unit = super.postStop()

  override def decide(t: Throwable): Supervision.Directive = super.decide(t)

  override def restart(): Stage[Result, Response] = super.restart()

}

object DispatchStep {

  def apply(): DispatchStep = new DispatchStep()

}