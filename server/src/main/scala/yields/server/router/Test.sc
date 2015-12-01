import scala.collection.immutable.Queue


val x = Queue.empty[Int]

x.enqueue(2).head
x.enqueue(2).enqueue(3).dequeue