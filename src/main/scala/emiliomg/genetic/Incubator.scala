package emiliomg.genetic

import emiliomg.genetic.util.Hamming.HammingComparable

import scala.annotation.tailrec
import scala.util.Random

trait Incubator {
  val target: String

  def run(startsWith: String): Unit
}

/*
 * TODO:
 *  - BE ABLE TO RANDOM CREATE SPACES!!!
 *  - generalize: String -> T
 *  - optimize mutationProbability (perhaps via a meta-genetic-algorithm? ;-) )
 *  - optimize breeding: randomize breeding partners instead of round-robin
 *  - optimize mutation:
 *    - use weighted probability for dropping char / creating new char instead of Random.nextBoolean
 *    - use weighted value to append chars
 */

class DefaultIncubator(val target: String) extends Incubator {

  // configuration variables
  val generationSize: Int = 10
  val numFittestToReproduce = 1
  val mutationProbability = 0.1

  require(generationSize > numFittestToReproduce, "number of fittest elements to take for reproduction must be smaller than whole generation")

  def run(startWith: String = ""): Unit = {
    val firstGeneration: Generation = Generation(
      generationSize,
      startWith,
      fitness = _.hammingDistanceTo(target) < _.hammingDistanceTo(target)
    )

    incubate(firstGeneration)
  }

  @tailrec
  private def incubate(gen: Generation, step: Int = 0): Unit = {
    val newGeneration = gen
      .takeFittest(take = numFittestToReproduce)
      .breed(targetSize = generationSize)
      .mutate(excludeFirst = numFittestToReproduce, probability = mutationProbability) // don't mutate the fittest individuals, keep them as they are
      .log(step.toString)

    if (newGeneration.fittest == target) println("Done")
    else incubate(newGeneration, step + 1)
  }
}

case class Generation(gen: Array[String], fitness: (String, String) => Boolean) {

  def takeFittest(take: Int): Generation = this.copy(gen = gen.take(take))

  def breed(targetSize: Int): Generation = {
    val newChildren = (gen.length until targetSize).map { i =>
      val parentAIdx = i % gen.length
      val parentBIdx = (parentAIdx + 1) % gen.length

      val child = gen(parentAIdx).map(Option.apply)
        .zipAll(gen(parentBIdx).map(Option.apply), None, None)
        .flatMap {
          case (a, b) => if (Random.nextBoolean()) a else b
        }
        .mkString

      child
    }

    this.copy(gen = gen ++ newChildren).sortByFitness
  }

  def mutate(excludeFirst: Int, probability: Double): Generation = {
    val protectedChildren = gen.take(excludeFirst)
    val mutatedChildren = gen.drop(excludeFirst).map { child: String =>
      val childWithMaybeLessChars = child.map { c: Char =>
        if (Random.nextDouble() < probability) {
          if (Random.nextBoolean()) Some(Random.nextPrintableChar())
          else None
        }
        else Some(c)
      }
      val maybeMoreChars = (0 to Random.nextInt(5)).map { _ =>
        if (Random.nextDouble() < probability) Some(Random.nextPrintableChar())
        else None
      }

      (childWithMaybeLessChars ++ maybeMoreChars).flatten.mkString
    }

    this.copy(gen = protectedChildren ++ mutatedChildren).sortByFitness
  }

  def fittest: String = this.sortByFitness.gen.head

  def log(description: String): Generation = {
    print(s"Logging Generation '$description': ")
    gen.foreach(x => print(x + ", "))
    print('\n')
    this
  }

  def sortByFitness: Generation = this.copy(gen = gen.sortWith(fitness))
}

object Generation {
  def apply(generationSize: Int, startWith: String, fitness: (String, String) => Boolean) = new Generation(Array.fill(generationSize)(startWith), fitness)

  def apply(gen: Array[String], fitness: (String, String) => Boolean) = new Generation(gen, fitness)
}
