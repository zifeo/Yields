package yields.server.router

import akka.actor._
import akka.util.ByteString
import yields.server.actions.Broadcast
import yields.server.dbi.models.UID
import yields.server.utils.FaultTolerance

import scala.language.postfixOps
import scala.util.{Failure, Try}

/**
  * Actor in charge of recording connection statuses and distributing push notifications to them.
  * TODO: further improvement such as push feedback
  */
final class Dispatcher() extends Actor with ActorLogging {

  import ClientHub._
  import Dispatcher._

  private type Pool = (Map[UID, ActorRef], Map[ActorRef, UID])
  private val uidPattern = """"uid":"""

  /** Empty pool at start. */
  def receive: Receive =
    state((Map.empty, Map.empty))

  /**
    * Current state of the pool.
    * @param pool uid to client hub index
    * @return new state
    */
  def state(pool: Pool): Receive = {

    // ----- ClientHub letters -----

    case InitConnection(uid) =>
      val newPool = add(uid, sender(), pool)
      context become state(newPool)

    case TerminateConnection =>
      val clientHub = sender()
      remove(clientHub, pool) match {
        case Some(newPool) =>
          context.become(state(newPool))

        case None =>
          log.warning(s"dispatch pool: terminate connection without uid")
      }

    case Notify(uids, broadcast) =>
      val users = uids.mkString("[", ",", "]")
      log.debug(s"dispatch pool: notify $users with $broadcast")
      filter(uids, pool).foreach(_ ! OnPush(broadcast))

    // ----- Default letters -----

    case unexpected =>
      log.warning(s"unexpected letter: $unexpected")

  }

  override val supervisorStrategy = FaultTolerance.nonFatalResume(log)

  /** Parse user id from given data. */
  private def parse(data: ByteString): Try[UID] = {
    val message = data.utf8String
    val pos = message.indexOf(uidPattern)
    if (pos >= 0) {
      val uid = message.drop(pos + uidPattern.length).takeWhile(_ != ',').trim
      Try(uid.toLong)
    } else Failure(new NoSuchElementException(s"no uid found: $message"))
  }

  /** Add uid - actor pair to pool. */
  private def add(uid: UID, client: ActorRef, pool: Pool): Pool = {
    val (index, reverseIndex) = pool
    log.debug(s"dispatch pool: + $uid")
    (index + (uid -> client)) -> (reverseIndex + (client -> uid))
  }

  /** Remove client from pool. */
  private def remove(client: ActorRef, pool: Pool): Option[Pool] = {
    val (index, reverseIndex) = pool
    reverseIndex.get(client) map { uid =>
      log.debug(s"dispatch pool: - $uid")
      (index - uid) -> (reverseIndex - client)
    }
  }

  /** Filter uids by removing ones that are not present in the pool. */
  private def filter(uids: List[UID], pool: Pool): List[ActorRef] =
    uids.flatMap(pool._1.get)

}

/** [[Dispatcher]] companion object. */
object Dispatcher {

  /** Sent each time a new connection happen and received with a message. */
  private[router] case class InitConnection(uid: UID)

  /** Sent each time a connection is terminated. */
  private[router] case class TerminateConnection()

  /** Sent each time a push notification is requested. */
  private[server] case class Notify(uids: List[UID], result: Broadcast)

  /** Creates a dispatcher props . */
  def props: Props =
    Props(classOf[Dispatcher])

}
