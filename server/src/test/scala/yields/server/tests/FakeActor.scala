package yields.server.tests

import akka.actor.{Props, ActorRef, Actor}

import scala.collection.mutable

/** Fakes an actor by registering all received messages. */
class FakeActor(receiver: ActorRef) extends Actor {

  val history = mutable.ListBuffer.empty[Any]

  /** Register a receiving function. */
  def register(receive: Receive): Unit =
    context become receive

  override def receive: Receive = {
    case letter => history += letter
  }

}

/** [[FakeActor]] companion object. */
object FakeActor {

  /** Creates a fake actor. */
  def props(receiver: ActorRef): Props =
    Props(classOf[FakeActor], receiver)

}