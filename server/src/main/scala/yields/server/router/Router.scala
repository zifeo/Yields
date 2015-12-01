package yields.server.router

import java.net.InetSocketAddress

import akka.actor.SupervisorStrategy.{Escalate, Resume}
import akka.actor._
import akka.io.{IO, Tcp}
import akka.stream.{OverflowStrategy, ActorMaterializer}
import akka.stream.actor.{ActorPublisher, ActorSubscriber}
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.util.ByteString
import yields.server.utils.Config

import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.control.NonFatal

/**
  * Actor in charge of handling connections and creating a client hub.
  * @param stream streaming operations pipeline
  * @param materializer stream materializer
  */
final class Router(val stream: Flow[ByteString, ByteString, Unit], private val dispatcher: ActorRef,
                   private implicit val materializer: ActorMaterializer)
  extends Actor with ActorLogging {

  import Tcp._
  import context.system

  val pipelineBuffer = Config.getInt("pipeline.buffer")

  override val supervisorStrategy = OneForOneStrategy(maxNrOfRetries = 3, withinTimeRange = 1 minute) {
    case NonFatal(nonfatal) =>
      val message = nonfatal.getMessage
      log.error(nonfatal, s"non fatal: $message")
      Resume
    case fatal =>
      val message = fatal.getMessage
      log.error(fatal, s"fatal: $message")
      Escalate
  }

  override def preStart(): Unit = {
    IO(Tcp) ! Bind(
      handler = self,
      localAddress = new InetSocketAddress(Config.getString("addr"), Config.getInt("port")),
      options = List(SO.KeepAlive(on = false), SO.TcpNoDelay(on = true)),
      backlog = Config.getInt("tcp.buffer"),
      pullMode = false
    )
  }

  def receive: Receive = {

    // ----- TCP letters -----

    case Bound(_) => log.info("System ready & bound.")

    case CommandFailed(Bind(_, _, _, _, _)) => context stop self

    case Connected(clientAddr, _) =>

      val socket = sender()
      val bindings = context.actorOf(ClientHub.props(socket, clientAddr, dispatcher))

      val pub = ActorPublisher[ByteString](bindings)
      val sub = ActorSubscriber[ByteString](bindings)
      Source(pub).buffer(pipelineBuffer, OverflowStrategy.fail).via(stream).to(Sink(sub)).run()

      socket ! Register(
        handler = bindings,
        keepOpenOnPeerClosed = false,
        useResumeWriting = true
      )

    // ----- Default letters -----

    case unexpected => log.warning(s"unexpected letter: $unexpected")

  }

}

/** [[Router]] companion object. */
object Router {

  /** Creates a router props with a materializer. */
  def props(stream: Flow[ByteString, ByteString, Unit], dispatcher: ActorRef)
           (implicit materializer: ActorMaterializer): Props =
    Props(classOf[Router], stream, dispatcher, materializer)

}