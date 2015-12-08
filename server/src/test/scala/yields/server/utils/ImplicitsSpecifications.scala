package yields.server.utils

import yields.server.tests.YieldsPropsSpec
import yields.server.utils.Implicits._

class ImplicitsSpecifications extends YieldsPropsSpec {

  property("Unzip4") {
    forAll() { (xs: List[(Int, Boolean, Double, Long)]) =>
      val (as, bs, cs, ds) = xs.unzip4
      as.zip(bs).zip(cs).zip(ds).zip(xs).forall {
        case ((((a, b), c), d), x) =>
          (a, b, c, d) == x
      }
    }
  }

  property("Unzip5") {
    forAll() { (xs: List[(Int, Boolean, Double, Long, Char)]) =>
      val (as, bs, cs, ds, es) = xs.unzip5
      as.zip(bs).zip(cs).zip(ds).zip(es).zip(xs).forall {
        case (((((a, b), c), d), e), x) =>
          (a, b, c, d, e) == x
      }
    }
  }

  property("Unzip6") {
    forAll() { (xs: List[(Int, Boolean, Double, Long, Char, Byte)]) =>
      val (as, bs, cs, ds, es, fs) = xs.unzip6
      as.zip(bs).zip(cs).zip(ds).zip(es).zip(fs).zip(xs).forall {
        case ((((((a, b), c), d), e), f), x) =>
          (a, b, c, d, e, f) == x
      }
    }
  }

}