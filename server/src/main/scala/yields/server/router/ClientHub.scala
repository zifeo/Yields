package yields.server.router

import java.net.InetSocketAddress
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit

import akka.actor._
import akka.event.{DiagnosticLoggingAdapter, Logging}
import akka.io.Tcp
import akka.stream.actor._
import akka.util.ByteString
import yields.server.actions.Broadcast
import yields.server.actions.users.UserConnectRes
import yields.server.io._
import yields.server.mpi.{Metadata, Notification, Response}
import yields.server.pipeline.blocks.SerializationModule
import yields.server.utils.{FaultTolerance, Temporal}

import scala.collection.immutable.Queue
import scala.collection.mutable
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
                     ) extends Actor with ActorPublisher[ByteString] with ActorSubscriber {

  import ActorPublisherMessage._
  import ActorSubscriberMessage._
  import ClientHub._
  import Dispatcher._
  import SerializationModule._
  import Tcp._

  val errorMessage = ByteString("""{"kind":"error"}""")
  val log: DiagnosticLoggingAdapter = Logging(this)
  val defaultMdc: Logging.MDC = Map("client" -> address)
  val tcpHistory = mutable.ListBuffer.empty[(String, OffsetDateTime)]

  log.mdc(defaultMdc)

  def receive: Receive =
    state(Queue.empty, identified = false)

  /** Casual state. It is dispatched if dispatcher has been linked. */
  def state(buffer: Queue[ByteString], identified: Boolean): Receive = {

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
      tcpHistory += "out" -> Temporal.now
      send(data, buffer, identified)

    case OnError(cause: Throwable) => // Processing error message
      log.error(cause, "error letter")
      send(errorMessage, buffer, identified)

    // ----- ClientHub letters -----

    case OnPush(broadcast) => // Notification
      val notification = Notification(broadcast, Metadata.now(0))
      log.debug(s"[BRD] $notification")
      val notificationSerialized = serialize(notification)
      val notificationSerializedMessage = notificationSerialized.utf8String
      log.debug(s"[BRD] $notificationSerializedMessage")
      tcpHistory += "brd" -> Temporal.now
      send(notificationSerialized, buffer, identified)

    case WriteAck(data) => // Confirm send
      confirm(data, buffer, identified)

    // ----- TCP letters -----

    case Received(data) => // Incoming message
      val incoming = data.utf8String
      log.debug(s"[INP] $incoming")
      tcpHistory += "inp" -> Temporal.now
      onNext(data)

    case PeerClosed => // Client exited
      log.info("peer closed")
      terminate()

    case ErrorClosed(cause) =>
      log.error(cause, s"error closed letter")
      terminate()

    case CommandFailed(Write(data, event)) =>
      val failing = data.utf8String
      log.error(s"write failed: $event $failing")

    // ----- Default -----

    case unexpected =>
      log.warning(s"unexpected letter: unexpected")

  }

  override val supervisorStrategy =
    FaultTolerance.nonFatalResumeOrEscalate(log)

  override def preStart(): Unit = {
    log.info("connected")
    tcpHistory += "connected" -> Temporal.now
  }

  override def postStop(): Unit = {
    log.info("disconnected")
    val totalInp = tcpHistory.count(_._1 == "inp")
    val totalOut = tcpHistory.count(_._1 == "out")
    val totalBrd = tcpHistory.count(_._1 == "brd")
    val total = tcpHistory.size
    val first = tcpHistory.last._2
    val latest = tcpHistory.head._2
    val length = ChronoUnit.MINUTES.between(latest, first)
    log.info(
      s"""
         |stats
         |len:$length
         |tot:$total
         |inp:$totalInp
         |out:$totalOut
         |brd:$totalBrd
         |fir:$first
         |lat:$latest
       """.stripMargin)
  }

  /** Always ask for more so the pipeline can continually work. */
  override protected def requestStrategy: RequestStrategy = new RequestStrategy {
    override def requestDemand(remainingRequested: Int): Int = 1
  }

  /** Send a message to the socket if buffer empty otherwise buffer it. */
  private def send(data: ByteString, buffer: Queue[ByteString], identified: Boolean): Unit = {
    buffer.size match {
      case 0 => socket ! Write(data, WriteAck(data))
      case len if len > 5 => log.warning(s"queue already buffer: $len")
      case _ =>
    }
    context become state(buffer.enqueue[ByteString](data), identified)
  }

  /** Identify connection by catching [[UserConnectRes]]. */
  private def identify(data: ByteString, buffer: Queue[ByteString]): Unit =
    Try(deserialize[Response](data)) match {

      case Success(Response(UserConnectRes(uid, _), _)) =>
        log.mdc(defaultMdc + ("user" -> uid))
        dispatcher ! InitConnection(uid)
        val newState = state(buffer, identified = true)
        newState(OnNext(data)) // will modify context hereafter

      case _ =>
        val message = data.utf8String
        log.warning(s"first request was not user connect: $message")
        send(errorMessage, buffer, identified = false)

    }

  /** Confirm message sending. */
  private def confirm(data: ByteString, buffer: Queue[ByteString], identified: Boolean): Unit = {
    val newBuffer = Try(buffer.dequeue) match {

      case Success((`data`, Queue())) =>
        Queue.empty

      case Success((`data`, remaining)) =>
        socket ! Write(remaining.head, WriteAck(remaining.head))
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
    context become state(newBuffer, identified)
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
  private[router] case class WriteAck(data: ByteString) extends Tcp.Event

  /** Creates a router props with a materializer. */
  def props(socket: ActorRef, address: InetSocketAddress, dispatcher: ActorRef): Props =
    Props(classOf[ClientHub], socket, address, dispatcher)

}
