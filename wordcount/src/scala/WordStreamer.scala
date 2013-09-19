package net.drib.sumdroppings.wordcount

import com.twitter.summingbird._
// import com.twitter.summingbird.scalding.{ WaitingState, Scalding, ScaldingStore, PipeFactory }
// import com.twitter.scalding.{Local, TextLine}
import com.twitter.summingbird.scalding._
import com.twitter.scalding._
import com.twitter.summingbird.batch._
import java.util.Date
import com.twitter.algebird.Interval

// This is not really usable, just a mock that does the same state over and over
class LoopState[T](init: Interval[T]) extends WaitingState[T] { self =>
  def begin = new RunningState[T] {
    def part = self.init
    def succeed(nextStart: Interval[T]) = self
    def fail(err: Throwable) = {
      println(err)
      self
    }
  }
}

object WordStreamer {

  def wordCount[P <: Platform[P]](
    source: Producer[P, String],
    store: P#Store[String, Long]) =
      source
        .flatMap { line => line.split("\\s+").map(_ -> 1L) }
        .sumByKey(store)

  def main(args: Array[String]) {
    val input = args(0)
    val output = args(1)
    println ("Running: " + input + " --> " + output)
    // dummy extractor
    implicit def extractor[T]: TimeExtractor[T] = TimeExtractor(_ => 0L)
    val src: Producer[Scalding, String] =
      Producer.source[Scalding, String](Scalding.pipeFactoryExact[String]( _ => TextLine("pathToInput")))
    val batcher: Batcher = Batcher.ofHours(1)
    val vbs = new VersionedBatchStore[String, Long, String, Long](output,
      3, batcher)(null)(identity)
    // val istore = VersionedBatchStore[String, Long]("myStorePath", 3)(_._2)(identity)
    val store: Scalding#Store[String,Long] = new InitialBatchedStore(batcher.currentBatch - 2L, vbs)
    val intr = Interval.leftClosedRightOpen(0L, 5L)
    var ws: WaitingState[java.util.Date] = new LoopState(intr.mapNonDecreasing(t => new Date(t)))
    val counter = wordCount[Scalding](src, store)
    ws = new Scalding("wordcount").run(ws, Local(true), counter)
  }

}
