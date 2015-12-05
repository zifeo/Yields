package yields.server.router

import java.net.InetSocketAddress

import akka.actor._
import akka.io.Tcp
import akka.stream.actor._
import akka.util.ByteString
import org.slf4j.MDC
import yields.server.actions.Broadcast
import yields.server.actions.users.UserConnectRes
import yields.server.mpi.{Metadata, Notification, Response}
import yields.server.pipeline.blocks.SerializationModule
import yields.server.utils.FaultTolerance
import yields.server.io._

import scala.collection.immutable.Queue
import scala.language.postfixOps
import scala.util.{Failure, Success, Try}

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
  import SerializationModule._
  import Tcp._

  MDC.put("client", address.toString)
  val errorMessage = """{"kind":"error"}"""

  def receive: Receive =
    state(Queue.empty, identified = false)

  /** Casual state. It is dispatched if dispatcher has been linked. */
  def state(buffer: Queue[ByteString], identified: Boolean = true): Receive = {

    // ----- Publisher letters -----

    case Request(n: Long) => // Stream subscriber requests more elements

    case Cancel => // Stream subscriber cancels the subscription
      log.error("hub detected pipeline error")
      terminate()

    // ----- Subscriber letters -----

    case OnNext(data: ByteString) if ! identified => // First outgoing message
      identify(data, buffer)

    case OnNext(data: ByteString) => // Outgoing message
      val outgoing = data.utf8String
      log.debug(s"[OUT] $outgoing")
      send(data, buffer)

    case OnError(cause: Throwable) => // Processing error message
      log.error(cause, "error letter")
      send(errorMessage, buffer)

    // ----- ClientHub letters -----

    case OnPush(broadcast) => // Notification
      val notification = Notification(broadcast, Metadata.now(0))
      log.debug(s"[BRD] $notification")
      send(serialize(notification), buffer)

    case Ack(data) => // Confirm send
      confirm(data, buffer)

    // ----- TCP letters -----

    case Received(data) => // Incoming message
      val incoming = data.utf8String
      log.debug(s"$address [INP] $incoming")
      onNext(data)

    case PeerClosed => // Client exited
      terminate()

    case ErrorClosed(cause) =>
      log.error(cause, s"error closed letter")
      terminate()

    case CommandFailed(Write(data, event)) =>
      val failing = data.utf8String
      log.error(s"write failed: $event $failing")

    // ----- Default -----

    case unexpected =>
      log.warning(s"unexpected letter: $unexpected")

  }

  override val supervisorStrategy =
    FaultTolerance.nonFatalResume(log)

  override def preStart(): Unit =
    log.info("connected")

  override def postStop(): Unit =
    log.info("disconnected")

  /** Returns the number of received request at the moment of the latest result. */
  override protected def requestStrategy: RequestStrategy = new RequestStrategy {
    override def requestDemand(remainingRequested: Int): Int = 1
  }

  /** Send a message to the socket if buffer empty otherwise buffer it. */
  private def send(data: String, buffer: Queue[ByteString]): Unit =
    send(ByteString(data), buffer)

  /** Send a message to the socket if buffer empty otherwise buffer it. */
  private def send(data: ByteString, buffer: Queue[ByteString]): Unit = {
    buffer.size match {
      case 0 => socket ! Write(data, Ack(data))
      case len if len > 5 => log.warning(s"queue already buffer: $len")
      case _ =>
    }
    context become state(buffer.enqueue[ByteString](data))
  }

  /** Identify connection by catching [[UserConnectRes]]. */
  private def identify(data: ByteString, buffer: Queue[ByteString]): Unit =
    Try(deserialize[Response](data)) match {

      case Success(Response(UserConnectRes(uid, _), _)) =>
        MDC.put("user", uid.toString)
        dispatcher ! InitConnection(uid)

        val newState = state(buffer)
        newState(data)
        context become newState

      case _ =>
        val message = data.utf8String
        log.warning(s"first request was not user connect: $message")
        send(errorMessage, buffer)

    }

  /** Confirm message sending. */
  private def confirm(data: ByteString, buffer: Queue[ByteString]): Unit = {
    val newBuffer = Try(buffer.dequeue) match {

      case Success((`data`, Queue())) =>
        Queue.empty

      case Success((`data`, remaining)) =>
        socket ! Write(remaining.head, Ack(remaining.head))
        remaining

      case Success((expected, remaining)) =>
        val receivedMessage = data.utf8String
        val expectedMessage = expected.utf8String
        log.warning(s"ack message not in the buffer, received: $receivedMessage")
        log.warning(s"ack message not in the buffer, expected: $expectedMessage")
        remaining

      case Failure(_) =>
        val message = data.utf8String
        log.warning(s"unexpected buffer empty on Ack for: $message")
        Queue.empty

    }
    context become state(newBuffer)
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
  private[router] case class OnPush(broadcast: Broadcast)

  /** Confirms a write. */
  private[router] case class Ack(data: ByteString) extends Tcp.Event

  /** Creates a router props with a materializer. */
  def props(socket: ActorRef, address: InetSocketAddress, dispatcher: ActorRef): Props =
    Props(classOf[ClientHub], socket, address, dispatcher)

}
