package emiliomg.genetic.util

import emiliomg.genetic.util.Hamming.HammingComparable
import org.scalacheck.Gen
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{FlatSpec, Matchers}

class HammingTest extends FlatSpec with Matchers with GeneratorDrivenPropertyChecks {

  "Hamming Distance" should "calculate correct distances" in {
    forAll((Gen.alphaNumStr, "string"), (Gen.chooseNum(0, 10), "expected distance")) { (string: String, expectedDistance: Int) ⇒
      whenever(expectedDistance >= 0 && expectedDistance < string.length) {
        val scrambledString: String = (for {
          reduceBy ← Gen.chooseNum(0, expectedDistance)
          reducedString = string.dropRight(reduceBy)
          scrambledString = scrambleString(reducedString, expectedDistance - reduceBy)
        } yield scrambledString).sample.get // .sample.get is ugly, how can it be done in a better way?

        string.hammingDistanceTo(scrambledString) shouldEqual expectedDistance
      }
    }
  }

  def scrambleString(data: String, numChars: Int): String = {
    (0 until numChars).foldLeft(data){(newString, pos) ⇒
      val newChar = {
        val oldChar = newString.charAt(pos)
        Gen.alphaNumChar.retryUntil(_ != oldChar).sample.get // .get should work since we disallow only a single char
      }
      newString.updated(pos, newChar)
    }
  }
}
