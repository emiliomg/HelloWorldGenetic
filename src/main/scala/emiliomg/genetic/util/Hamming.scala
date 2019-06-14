package emiliomg.genetic.util

object Hamming {
  implicit class HammingComparable(a: String) {
    def hammingDistanceTo(b: String): Int = {
      a.map(Option.apply).zipAll(b.map(Option.apply), None, None).map{
        case (Some(x), Some(y)) ⇒ if (x == y) 0 else 1
        case _ ⇒ 1 // (None, None) should never happen
      }.sum
    }
  }
}
