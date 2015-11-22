package yields.server.router

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.util.ByteString
import yields.server.dbi.models.UID

/**
  * Actor in charge of recording connection statuses and distributing push notifications to them.
  * TODO : further improvement such as push feedback
  */
final class Dispatcher() extends Actor with ActorLogging {

  import Dispatcher._

  val FindUID = """.+"metadata".+"client":\s?([0-9]+).+""".r

  def receive = state(Map.empty)

  /**
    * Current state of the pool.
    * TODO : find a double indexed map
    * @param pool uid to client hub index
    * @return new state
    */
  def state(pool: Map[UID, ActorRef]): Receive = {

    case InitConnection(data) =>
      data.utf8String match {
        case FindUID(uid) =>
          val clientHub = sender()
          log.debug(s"dispatch pool: +$uid")
          context.become(state(pool + (uid.toLong -> clientHub)))
        case _ => log.warning(s"init connection without metadata.client ${data.utf8String}")
      }

    case TerminateConnection() =>
      pool.find(_._2 == sender()) match {

        case Some((uid, _)) =>
          log.debug(s"dispatch pool: -$uid")
          context.become(state(pool - uid))

        case None =>
          log.warning(s"terminate not existing connection")
      }

    case x =>
      log.warning(s"unexpected letter received: $x")

  }

}

/** [[Dispatcher]] companion object. */
object Dispatcher {

  /** Sent each time a new connection happen and received with a message. */
  case class InitConnection(data: ByteString)

  /** Sent each time a connection is terminated. */
  case class TerminateConnection()

  /** Creates a dispatcher props . */
  def props: Props =
    Props(classOf[Dispatcher])

}