package yields.server.router

import java.net.InetSocketAddress

import akka.actor.SupervisorStrategy.{Escalate, Resume}
import akka.actor._
import akka.io.Tcp
import akka.stream.actor._
import akka.util.ByteString

import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.control.NonFatal

/**
  * Actor in charge of handling a **single** client request and answering with corresponding response.
  * @param socket actor representing the TCP connection between the client and the server.
  * @param address ip and port of this client for logging purposes
  */
final class ClientHub(private val socket: ActorRef,
                      private val address: InetSocketAddress,
                      private val dispatcher: ActorRef
                     ) extends Actor with ActorLogging with ActorPublisher[ByteString] with ActorSubscriber {

  import ActorPublisherMessage._
  import ActorSubscriberMessage._
  import ClientHub._
  import Dispatcher._
  import Tcp._

  override val supervisorStrategy = OneForOneStrategy(maxNrOfRetries = 3, withinTimeRange = 1 minute) {
    case NonFatal(nonfatal) =>
      val message = nonfatal.getMessage
      log.error(nonfatal, s"$address non fatal: $message")
      Resume
    case fatal =>
      val message = fatal.getMessage
      log.error(fatal, s"$address fatal: $message")
      Escalate
  }

  override def preStart(): Unit = {
    log.info(s"connected $address.")
  }

  override def postStop(): Unit = {
    log.info(s"disconnected $address.")
  }

  /** This state aims to capture the first message in order to identify the user. */
  def receive: Receive = {

    // ----- Publisher letters -----

    case Request(n: Long) => // Stream subscriber requests more elements.

    case Received(data) =>
      val incoming = data.utf8String
      log.debug(s"$address [INP] (not yet alive) $incoming")
      onNext(data)
      dispatcher ! InitConnection(data)
      context.become(alive)

    // ----- Default -----

    case unexpected => log.warning(s"unexpected letter (not yet alive): $unexpected")

  }

  /** Casual state. */
  def alive: Receive = {

    // ----- Publisher letters -----

    case Request(n: Long) => // Stream subscriber requests more elements.

    case Cancel => // Stream subscriber cancels the subscription.
      log.error(s"$address hub detected pipeline error")
      dispatcher ! TerminateConnection
      context stop self

    // ----- Subscriber letters -----

    case OnNext(data: ByteString) =>
      val outgoing = data.utf8String
      log.debug(s"$address [OUT] $outgoing")
      socket ! Write(data)

    case OnNext(element) => log.warning(s"$address unexpected onNext letter: $element")

    case OnComplete => log.warning(s"$address unexpected completed letter")

    case OnError(cause: Throwable) =>
      val message = cause.getMessage
      log.error(cause, s"$address error letter: $message")
      socket ! Write(ByteString("""{"kind":"error"}"""))

    // ----- ClientHub letters -----

    case OnPush(data) => // Notification
      val outgoing = data.utf8String
      log.debug(s"$address [BRD] $outgoing")
      socket ! Write(data)

    // ----- TCP letters -----

    case Received(data) =>
      val incoming = data.utf8String
      log.debug(s"$address [INP] $incoming")
      onNext(data)

    case PeerClosed =>
      dispatcher ! TerminateConnection
      context stop self

    case ErrorClosed(cause) =>
      log.error(cause, s"$address error closed letter: $cause")
      dispatcher ! TerminateConnection
      context stop self

    // ----- Default -----

    case unexpected => log.warning(s"$address unexpected letter: $unexpected")

  }

  /** Returns the number of received request at the moment of the latest result. */
  override protected def requestStrategy: RequestStrategy = new RequestStrategy {
    override def requestDemand(remainingRequested: Int): Int = 1
  }

}

/** [[ClientHub]] companion object. */
object ClientHub {

  /** Handle a notification. */
  private[router] case class OnPush(data: ByteString)

  /** Creates a router props with a materializer. */
  def props(socket: ActorRef, address: InetSocketAddress, dispatcher: ActorRef): Props =
    Props(classOf[ClientHub], socket, address, dispatcher)

}
