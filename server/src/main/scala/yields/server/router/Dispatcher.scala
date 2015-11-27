package yields.server.router

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.util.ByteString
import yields.server.actions.{Broadcast, Result}
import yields.server.dbi.models.UID
import yields.server.mpi.{Notification, Metadata, Response}
import yields.server.pipeline.blocks.SerializationModule

/**
  * Actor in charge of recording connection statuses and distributing push notifications to them.
  * TODO : further improvement such as push feedback
  */
final class Dispatcher() extends Actor with ActorLogging {

  import ClientHub._
  import Dispatcher._

  val uidPattern = """"client":"""

  def receive = state(Map.empty)

  /**
    * Current state of the pool.
    * TODO : find a double indexed map
    * @param pool uid to client hub index
    * @return new state
    */
  def state(pool: Map[UID, ActorRef]): Receive = {

    case InitConnection(data) =>
      val message = data.utf8String
      val pos = message.indexOf(uidPattern)
      if (pos < 0) {
        log.warning(s"init connection without metadata.client")
      } else {
        val uid = message.drop(pos + uidPattern.length).takeWhile(_ != ',').trim.toInt
        val clientHub = sender()
        log.debug(s"dispatch pool: +$uid")
        context.become(state(pool + (uid.toLong -> clientHub)))
      }

    case TerminateConnection =>
      pool.find(_._2 == sender()) match {

        case Some((uid, _)) =>
          log.debug(s"dispatch pool: -$uid")
          context.become(state(pool - uid))

        case None =>
          log.warning(s"terminate non-existing connection")
      }

    case Notify(uids, result) =>
      log.debug(s"notify $uids with $result")
      val metadata = Metadata.now(0)
      val push = OnPush(SerializationModule.serialize(Notification(result, metadata)))
      uids.flatMap(pool.get).foreach(_ ! push)

    case x =>
      log.warning(s"unexpected letter received: $x")

  }

}

/** [[Dispatcher]] companion object. */
object Dispatcher {

  /** Sent each time a new connection happen and received with a message. */
  private[router] case class InitConnection(data: ByteString)

  /** Sent each time a connection is terminated. */
  private[router] case class TerminateConnection()

  /** Sent each time a push notification is requested. */
  private[server] case class Notify(uids: Seq[UID], result: Broadcast)

  /** Creates a dispatcher props . */
  def props: Props =
    Props(classOf[Dispatcher])

}