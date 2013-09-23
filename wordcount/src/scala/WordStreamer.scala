package net.drib.sumdroppings.wordcount

import com.twitter.summingbird._
// import com.twitter.summingbird.scalding.{ WaitingState, Scalding, ScaldingStore, PipeFactory }
// import com.twitter.scalding.{Local, TextLine}
import com.twitter.summingbird.scalding._
import com.twitter.scalding._
import com.twitter.summingbird.batch._
import java.util.Date
import com.twitter.algebird.Interval
import org.apache.hadoop.conf.Configuration

// This is not really usable, just a mock that does the same state over and over
class LoopState[T](init: Interval[T]) extends WaitingState[T] { self =>
  def begin = new RunningState[T] {
    def part = self.init
    def succeed(nextStart: Interval[T]) = self
    def fail(err: Throwable) = {
      println("LoopOops: " + err)
      err.printStackTrace(System.out)
      self
    }
  }
}


class WordStreamer(env: Env) extends AbstractJob(env) {
  val name = "WordStreamer"
  import WordStreamer._  // assumed to hold flatmapper and sources

  val input = "/user/tom/hqha/part-00000"
  val output = "/user/tom/testout.txt"

  implicit def extractor[T]: TimeExtractor[T] = TimeExtractor(_ => 5L)
  val src: Producer[Scalding, String] =
    Producer.source[Scalding, String](Scalding.pipeFactoryExact[String]( _ => TextLine("pathToInput")))

  implicit var batcher = Batcher.unit

  val vbs = new VersionedBatchStore[String, Long, String, Long](output,
    3, batcher)(null)(identity)
  // val istore = VersionedBatchStore[String, Long]("myStorePath", 3)(_._2)(identity)
  val store: Scalding#Store[String,Long] = new InitialBatchedStore(batcher.currentBatch - 2L, vbs)
  val intr = Interval.leftClosedRightOpen(new Date(0L), new Date(5L))
  var ws: WaitingState[java.util.Date] = new LoopState(intr)
  val counter = wordCount[Scalding](src, store)
  val job = new Scalding("wordcount")

  println("=== Constructor done ===")

  // implicit val batcher: Batcher = Batcher.ofHours(2)
  // Now, job creation is easy!
}

object WordStreamer {

  def wordCount[P <: Platform[P]](
    source: Producer[P, String],
    store: P#Store[String, Long]) =
      source
        .flatMap { line => line.split("\\s+").map(_ -> 1L) }
        .sumByKey(store)

  def main(args: Array[String]) {
    val input = if (args.length > 0) args(0) else "/user/tom/hqha/part-00000"
    val output = if (args.length > 1) args(1) else "/user/tom/testout.txt"
    println ("Running: " + input + " --> " + output)
    // dummy extractor
    implicit def extractor[T]: TimeExtractor[T] = TimeExtractor(_ => 5L)
    val src: Producer[Scalding, String] =
      Producer.source[Scalding, String](Scalding.pipeFactoryExact[String]( _ => TextLine("pathToInput")))
    // val batcher: Batcher = Batcher.ofHours(1)
    var batcher = Batcher.unit
    val vbs = new VersionedBatchStore[String, Long, String, Long](output,
      3, batcher)(null)(identity)
    // val istore = VersionedBatchStore[String, Long]("myStorePath", 3)(_._2)(identity)
    val store: Scalding#Store[String,Long] = new InitialBatchedStore(batcher.currentBatch - 2L, vbs)
    val intr = Interval.leftClosedRightOpen(new Date(0L), new Date(5L))
    var ws: WaitingState[java.util.Date] = new LoopState(intr)
    val counter = wordCount[Scalding](src, store)
    var conf = new Configuration;
    ws = new Scalding("wordcount").run(ws, Hdfs(true, conf), counter)
  }

}
