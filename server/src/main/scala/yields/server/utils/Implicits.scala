package yields.server.utils

import scala.collection.GenTraversable
import scala.language.higherKinds

/** Regroups all implicits. */
object Implicits {

  /** Custom generic traversable class */
  implicit class CustomGenericTraversable[+A](val gen: GenTraversable[A]) extends AnyVal {

    /** As unzip and unzip3, unzip4 divides a collection of 4-tuple into 4 collections. */
    def unzip4[A1, A2, A3, A4](implicit asTuple4: A => (A1, A2, A3, A4))
    : (List[A1], List[A2], List[A3], List[A4]) = {
      val b1 = List.newBuilder[A1]
      val b2 = List.newBuilder[A2]
      val b3 = List.newBuilder[A3]
      val b4 = List.newBuilder[A4]

      for (wxyz <- gen) {
        val (w, x, y, z) = asTuple4(wxyz)
        b1 += w
        b2 += x
        b3 += y
        b4 += z
      }
      (b1.result(), b2.result(), b3.result(), b4.result())
    }

    /** As unzip and unzip3, unzip5 divides a collection of 5-tuple into 5 collections. */
    def unzip5[A1, A2, A3, A4, A5](implicit asTuple5: A => (A1, A2, A3, A4, A5))
    : (List[A1], List[A2], List[A3], List[A4], List[A5]) = {
      val b1 = List.newBuilder[A1]
      val b2 = List.newBuilder[A2]
      val b3 = List.newBuilder[A3]
      val b4 = List.newBuilder[A4]
      val b5 = List.newBuilder[A5]

      for (vwxyz <- gen) {
        val (v, w, x, y, z) = asTuple5(vwxyz)
        b1 += v
        b2 += w
        b3 += x
        b4 += y
        b5 += z
      }
      (b1.result(), b2.result(), b3.result(), b4.result(), b5.result())
    }

    /** As unzip and unzip3, unzip6 divides a collection of 6-tuple into 6 collections. */
    def unzip6[A1, A2, A3, A4, A5, A6](implicit asTuple6: A => (A1, A2, A3, A4, A5, A6))
    : (List[A1], List[A2], List[A3], List[A4], List[A5], List[A6]) = {
      val b1 = List.newBuilder[A1]
      val b2 = List.newBuilder[A2]
      val b3 = List.newBuilder[A3]
      val b4 = List.newBuilder[A4]
      val b5 = List.newBuilder[A5]
      val b6 = List.newBuilder[A6]

      for (uvwxyz <- gen) {
        val (u, v, w, x, y, z) = asTuple6(uvwxyz)
        b1 += u
        b2 += v
        b3 += w
        b4 += x
        b5 += y
        b6 += z
      }
      (b1.result(), b2.result(), b3.result(), b4.result(), b5.result(), b6.result())
    }

  }

}
