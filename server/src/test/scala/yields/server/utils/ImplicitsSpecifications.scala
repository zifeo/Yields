package yields.server.utils

import org.scalacheck.{Prop, Properties}
import yields.server.utils.Implicits._

object ImplicitsSpecifications extends Properties("ImplicitsUtils") {

  import Prop.forAll

  property("Unzip5") = forAll { (xs: List[(Int, Boolean, Double, Long, Char)]) =>
    val (as, bs, cs, ds, es) = xs.unzip5
    as.zip(bs).zip(cs).zip(ds).zip(es).zip(xs).forall {
      case (((((a, b), c), d), e), x) =>
        (a, b, c, d, e) == x
    }
  }

}