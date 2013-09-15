package net.drib.sumdroppings.wordcount

import com.twitter.summingbird._
import com.twitter.summingbird.scalding.{ WaitingState, Scalding, ScaldingStore, PipeFactory }
import com.twitter.scalding.{Local}
import java.util.Date

object WordStreamer {

  def wordCount[P <: Platform[P]](
    source: Producer[P, String],
    store: P#Store[String, Long]) =
      source
        .flatMap { line => line.split("\\s+").map(_ -> 1L) }
        .sumByKey(store)

  def main(args: Array[String]) {
    val src: Producer[Scalding, String] = null
    val dst: ScaldingStore[String, Long] = null
    var ws: WaitingState[Date] = null
    // Oops, this doesn't work
    // val counter = wordCount(src, dst)
    // Let's use this as a placeholder
    val counter: PipeFactory[_] = null
    ws = new Scalding("wordcount").run(ws, Local(true), counter)
  }

}
