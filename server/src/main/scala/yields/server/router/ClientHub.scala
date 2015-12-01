package yields.server.router

import java.net.InetSocketAddress

import akka.actor.SupervisorStrategy.{Escalate, Resume}
import akka.actor._
import akka.io.Tcp
import akka.stream.actor._
import akka.util.ByteString
import org.slf4j.MDC

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

  MDC.put("client", address.toString)

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

  def receive: Receive = state(dispatched = false)

  /** Casual state. It is dispatched if dispatcher has been linked. */
  def state(dispatched: Boolean): Receive = {

    // ----- Publisher letters -----

    case Request(n: Long) => // Stream subscriber requests more elements.

    case Cancel => // Stream subscriber cancels the subscription.
      log.error(s"$address hub detected pipeline error")
      terminate()

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
      if (! dispatched) {
        dispatcher ! InitConnection(data)
        context.become(state(dispatched = true))
      }

    case PeerClosed =>
      terminate()

    case ErrorClosed(cause) =>
      log.error(cause, s"$address error closed letter: $cause")
      terminate()

    case CommandFailed(Write(data, event)) =>
      val failing = data.utf8String
      log.error(s"write failed: $event $failing")

    // ----- Default -----

    case unexpected => log.warning(s"$address unexpected letter: $unexpected")

  }

  /** Returns the number of received request at the moment of the latest result. */
  override protected def requestStrategy: RequestStrategy = new RequestStrategy {
    override def requestDemand(remainingRequested: Int): Int = 1
  }

  /** Close client hub and stop actor. */
  private def terminate(): Unit = {
    dispatcher ! TerminateConnection
    context stop self
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
