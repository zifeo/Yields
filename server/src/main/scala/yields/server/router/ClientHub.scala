package yields.server.router

import akka.actor.{Props, Actor, ActorLogging, ActorRef}
import akka.io.Tcp
import akka.stream.actor._
import akka.util.ByteString

/**
  * Actor in charge of handling a **single** client request and answering with corresponding response.
  * @param socket actor representing the TCP connection between the client and the server.
  * @param name name of this client hub used for logging purposes
  */
final class ClientHub(private val socket: ActorRef, private val name: String, private val dispatcher: ActorRef)
  extends Actor with ActorLogging with ActorPublisher[ByteString] with ActorSubscriber {

  import ActorPublisherMessage._
  import ActorSubscriberMessage._
  import Tcp._
  import Dispatcher._
  import ClientHub._

  private var count = 0

  /** Logs at debug level with client name prefixed. */
  def debug(channel: String, message: String): Unit = {
    log.debug(s"$channel $name $message")
  }

  def receive: Receive = born

  /** Starting state. */
  def born: Receive = {

    case Received(data) =>
      debug("[IN]", data.utf8String)
      count += 1
      onNext(data)
      dispatcher ! InitConnection(data)
      context.become(alive)

    case Request(_) =>
    // expected

    case x =>
      log.error(s"unexpected born letter: $x")

  }

  /** Casual state. */
  def alive: Receive = {

    case Received(data) =>
      debug("[IN]", data.utf8String)
      count += 1
      onNext(data)

    case OnNext(data: ByteString) =>
      debug("[OUT]", data.utf8String)
      count -= 1
      assert(count >= 0)
      socket ! Write(data)

    case OnPush(data) =>
      debug("[OUT]", data.utf8String)
      socket ! Write(data)

    case OnComplete =>
      debug("[OUT]", "completed letter /!\\")

    case OnError(cause) =>
      val trace = cause.getStackTrace.mkString("\n")
      debug("[OUT]", s"error letter /!\\: $cause \n $trace")
      socket ! Write(ByteString("server error"))
    // TODO : improve error handling taking into account the supervisor too

    case Request(_) =>
    // onNext counterpart

    case PeerClosed | ErrorClosed(_) =>
      dispatcher ! TerminateConnection
      context stop self

    case x =>
      log.warning(s"unexpected letter received: $x")
  }

  /** Returns the number of received request at the moment of the latest result. */
  override protected def requestStrategy: RequestStrategy = new RequestStrategy {
    override def requestDemand(remainingRequested: Int): Int = count
  }

}

/** [[ClientHub]] companion object. */
object ClientHub {

  /** Handle a notification. */
  private[router] case class OnPush(data: ByteString)

  /** Creates a router props with a materializer. */
  def props(socket: ActorRef, name: String, dispatcher: ActorRef): Props =
    Props(classOf[ClientHub], socket, name, dispatcher)

}
