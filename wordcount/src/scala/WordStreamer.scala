package net.drib.sumdroppings.wordcount

import com.twitter.summingbird._
// import com.twitter.summingbird.scalding.{ WaitingState, Scalding, ScaldingStore, PipeFactory }
// import com.twitter.scalding.{Local, TextLine}
import com.twitter.summingbird.scalding._
import com.twitter.scalding._
import com.twitter.summingbird.batch._
import java.util.Date

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
    val src: Producer[Scalding, String] =
      Producer.source(Scalding.pipeFactoryExact[String]( _ => TextLine("pathToInput")))
    val istore = VersionedBatchStore[String, Long](output, 3)(_._2)
    implicit val batcher: Batcher = Batcher.ofHours(1)
    val store = new InitialBatchedStore(batcher.currentBatch - 2L, istore)
    //TODO: need a real WaitingState
    var ws: WaitingState[Date] = null
    val counter = wordCount(src, store)
    ws = new Scalding("wordcount").run(ws, Local(true), counter)
  }

}
