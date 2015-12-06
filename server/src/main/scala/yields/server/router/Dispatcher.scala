package yields.server.router

import akka.actor._
import yields.server.actions.Broadcast
import yields.server.dbi.models.UID
import yields.server.utils.FaultTolerance

import scala.language.postfixOps

/**
  * Actor in charge of recording connection statuses and distributing push notifications to them.
  */
final class Dispatcher() extends Actor with ActorLogging {

  import ClientHub._
  import Dispatcher._

  private type Pool = (Map[UID, Set[ActorRef]], Map[ActorRef, UID])

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
      val newPool = remove(sender(), pool)
      context become state(newPool)

    case Notify(uids, broadcast) =>
      val users = uids.mkString("[", ",", "]")
      log.debug(s"dispatch pool: notify $users with $broadcast")
      get(uids, pool).foreach(_ ! OnPush(broadcast))

    // ----- Default letters -----

    case unexpected =>
      log.warning(s"unexpected letter: $unexpected")

  }

  override val supervisorStrategy = FaultTolerance.nonFatalResume(log)

  /** Add uid - actor pair to pool. */
  private def add(uid: UID, client: ActorRef, pool: Pool): Pool = {
    val (index, reverseIndex) = pool

    val newEntry = index.get(uid) match {
      case Some(entry) => entry + client
      case None => Set(client)
    }
    val clientCount = newEntry.size
    log.debug(s"dispatch pool: + $uid (total $clientCount)")

    (index + (uid -> newEntry)) -> (reverseIndex + (client -> uid))
  }

  /** Remove client from pool. */
  private def remove(client: ActorRef, pool: Pool): Pool = {
    val (index, reverseIndex) = pool
    reverseIndex.get(client) match {
      case Some(uid) =>
        val newEntry = index(uid) - client
        val clientCount = newEntry.size
        log.debug(s"dispatch pool: - $uid (left $clientCount)")

        if (newEntry.isEmpty)
          (index - uid) -> (reverseIndex - client)
        else
          (index + (uid -> newEntry)) -> (reverseIndex - client)
      case None =>
        log.warning(s"dispatch pool: remove non-existing $client")
        pool
    }
  }

  /** Get by filtering existing uids in the pool and flatten all connections. */
  private def get(uids: List[UID], pool: Pool): List[ActorRef] = {
    val (index, _) = pool
    uids.flatMap(index.get).flatten
  }

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
