package yields.server

import java.net.InetSocketAddress
import java.util.logging.LogManager

import akka.actor._
import akka.io.Tcp._
import akka.io.{IO, Tcp}
import akka.stream._
import akka.stream.actor._
import akka.stream.scaladsl.{Sink, Source}
import akka.util.ByteString
import yields.server.pipeline.Pipeline
import yields.server.utils.Config

import scala.io.StdIn
import scala.util.control.NonFatal

/**
  * Yields server daemon.
  */
object Yields {

  // Configure logging with LogBack
  {
    val manager = LogManager.getLogManager
    manager.readConfiguration()
  }

  // Starts system and enable flow errors logging on demand
  private implicit lazy val system = ActorSystem("Yields-server")
  private implicit val materializer = {
    val decider: Supervision.Decider = {
      case NonFatal(e) =>
        val exception = e.getStackTrace.toList.headOption.getOrElse("error when getting the stacktrace")
        val message = e.getMessage
        system.log.error(s"$exception: $message")
        Supervision.stop
    }
    ActorMaterializer(ActorMaterializerSettings(system).withSupervisionStrategy(decider))
  }

  private val pipeline = Pipeline()

  /**
    * Launches the Yields app.
    * @param args no args
    */
  def main(args: Array[String]): Unit = {
    start()
    StdIn.readLine() // get rid of remaining input buffer
    StdIn.readLine() // wait on a new line for stopping
    close()
    dbi.close()
  }

  /**
    * Starts the server.
    * @return empty future representing server liveness
    */
  private[server] def start(): Unit = {

    // Setup networking and pipeline.
    /*
    val connections = scaladsl.Tcp().bind(
      interface = Config.getString("addr"),
      port = Config.getInt("port"),
      options = List(SO.KeepAlive(on = false), SO.TcpNoDelay(on = true)),
      halfClose = false,
      idleTimeout = Duration.Inf
    )
    val pipeline = Pipeline()
    */

    // Handles connections.
    /*
    connections runForeach { case IncomingConnection(_, remoteAddress, flow) =>
      system.log.info(s"connection from $remoteAddress")
      flow.join(pipeline).run()
    }
    */

    system.log.info("Server starting.")
    system.actorOf(Props(classOf[YieldsRouter]), "Yields-connection-manager")

  }

  /**
    * Actor in charge of handling connections and creating a client hub.
    */
  class YieldsRouter extends Actor with ActorLogging {

    override def preStart(): Unit = {
      IO(Tcp) ! Bind(
        handler = self,
        localAddress = new InetSocketAddress(Config.getString("addr"), Config.getInt("port")),
        options = List(SO.KeepAlive(on = false), SO.TcpNoDelay(on = true)),
        backlog = 100,
        pullMode = false
      )
    }

    def receive = {

      case Bound(_) =>
      case CommandFailed(Bind(_, addr, _, _, _)) => context stop self

      case Connected(clientAddr, _) =>
        log.info(s"connection from $clientAddr.")

        val socket = sender()
        val bindings = context.actorOf(Props(classOf[ClientHub], socket, clientAddr.toString))
        // one actor per client

        val pub = ActorPublisher[ByteString](bindings)
        val sub = ActorSubscriber[ByteString](bindings)
        Source(pub).via(pipeline).to(Sink(sub)).run()

        socket ! Register(
          handler = bindings,
          keepOpenOnPeerClosed = false,
          useResumeWriting = true
        )

      case x =>
        log.warning(s"unexpected letter received: $x")
    }

  }

  /**
    * Actor in charge of handling a **single** client request and answering with corresponding response.
    */
  class ClientHub(private val socket: ActorRef, private val name: String)
    extends Actor with ActorLogging with ActorPublisher[ByteString] with ActorSubscriber {

    import ActorPublisherMessage._
    import ActorSubscriberMessage._

    private var count = 0

    /** Logs at debug level with client name prefixed. */
    def debug(channel: String, message: String): Unit = {
      log.debug(s"$channel $name $message")
    }

    def receive = {
      case Received(data) =>
        debug("[IN]", data.utf8String)
        count += 1
        onNext(data)

      case OnNext(data: ByteString) =>
        debug("[OUT]", data.utf8String)
        count -= 1
        assert(count >= 0)
        socket ! Write(data)

      case OnComplete =>
        debug("[OUT]", "completed letter /!\\")

      case OnError(cause) =>
        debug("[OUT]", s"error letter /!\\: $cause")

      case Request(_) =>
        // onNext counterpart

      case PeerClosed =>
        context stop self

      case x =>
        log.warning(s"unexpected letter received: $x")
    }

    /** Returns the number of received request at the moment of the latest result. */
    override protected def requestStrategy: RequestStrategy = new RequestStrategy {
      override def requestDemand(remainingRequested: Int): Int = count
    }

  }

  /**
    * Closes the server (cannot be restart without full restart).
    * This include the actor system and the database.
    */
  private[server] def close(): Unit = {
    system.log.info("Server closing.")
    system.terminate()
  }

}
