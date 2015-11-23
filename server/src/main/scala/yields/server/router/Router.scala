package yields.server.router

import java.net.InetSocketAddress

import akka.actor.{ActorRef, Actor, ActorLogging, Props}
import akka.io.{IO, Tcp}
import akka.stream.ActorMaterializer
import akka.stream.actor.{ActorPublisher, ActorSubscriber}
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.util.ByteString
import yields.server.utils.Config

/**
  * Actor in charge of handling connections and creating a client hub.
  * @param stream streaming operations pipeline
  * @param materializer stream materializer
  */
final class Router(val stream: Flow[ByteString, ByteString, Unit], private val dispatcher: ActorRef,
                   private implicit val materializer: ActorMaterializer)
  extends Actor with ActorLogging {

  import Tcp._
  import Dispatcher._
  import context.system

  override def preStart(): Unit = {
    IO(Tcp) ! Bind(
      handler = self,
      localAddress = new InetSocketAddress(Config.getString("addr"), Config.getInt("port")),
      options = List(SO.KeepAlive(on = false), SO.TcpNoDelay(on = true)),
      backlog = 100,
      pullMode = false
    )
  }

  def receive: Receive = {

    case Bound(_) =>
    case CommandFailed(Bind(_, addr, _, _, _)) => context stop self

    case Connected(clientAddr, _) =>
      log.info(s"connection from $clientAddr.")

      val socket = sender()
      val bindings = context.actorOf(ClientHub.props(socket, clientAddr.toString, dispatcher))
      // one actor per client

      val pub = ActorPublisher[ByteString](bindings)
      val sub = ActorSubscriber[ByteString](bindings)
      Source(pub).via(stream).to(Sink(sub)).run()

      socket ! Register(
        handler = bindings,
        keepOpenOnPeerClosed = false,
        useResumeWriting = true
      )

    case x =>
      log.warning(s"unexpected letter received: $x")
  }

}

/** [[Router]] companion object. */
object Router {

  /** Creates a router props with a materializer. */
  def props(stream: Flow[ByteString, ByteString, Unit], dispatcher: ActorRef)
           (implicit materializer: ActorMaterializer): Props =
    Props(classOf[Router], stream, dispatcher, materializer)

}