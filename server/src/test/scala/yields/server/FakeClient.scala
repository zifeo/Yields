package yields.server

import java.io.{BufferedReader, BufferedWriter, InputStreamReader, OutputStreamWriter}
import java.net.Socket
import java.time.OffsetDateTime

import spray.json._
import yields.server.io._
import yields.server.actions.{Broadcast, Action}
import yields.server.dbi.models.UID
import yields.server.mpi.{Notification, Metadata, Request, Response}
import yields.server.utils.Config

import scala.collection.mutable
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/** Fakes a client connection through a socket. */
class FakeClient(val uid: UID) {

  private val socket = new Socket(Config.getString("addr"), Config.getInt("port"))
  private val receiver = new BufferedReader(new InputStreamReader(socket.getInputStream))
  private val sender = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream))

  val requests = mutable.Set.empty[Request]
  val responses = mutable.Set.empty[Response]
  val notifications = mutable.Set.empty[Notification]

  /** Send a request to the server. */
  def send(request: Request): Unit = {
    requests += request
    sender.write(request.toJson.toString())
    sender.newLine()
    sender.flush()
  }

  /** Send an action to the server. */
  def send(action: Action): OffsetDateTime = {
    val metadata = Metadata.now(uid)
    send(Request(action, metadata))
    metadata.ref
  }

  /** Gets next response from the server. */
  def receive(): Future[Response] = Future {
    val message = receiver.readLine()
    val response = message.parseJson.convertTo[Response]
    responses += response
    response
  }

  /** Gets next notification from the server. */
  def listen(): Future[Notification] = Future {
    val message = receiver.readLine()
    val notification = message.parseJson.convertTo[Notification]
    notifications += notification
    notification
  }

  /** Clean everything. */
  def close(): Unit = {
    requests.clear()
    responses.clear()
    notifications.clear()
    socket.close()
  }

}