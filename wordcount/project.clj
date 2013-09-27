(defproject wordcount "0.1.0-SNAPSHOT"
  :repositories [["conjars" "http://conjars.org/repo"]
                 ["sonatype" "http://oss.sonatype.org/content/repositories/releases"]
  		           ["twitter-repo" "http://maven.twttr.com"]]
  :plugins [[lein-scalac "0.1.0"]]
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.scala-lang/scala-library "2.9.3"]
                 [com.twitter/summingbird-core_2.9.3 "0.1.4"]
                 [com.twitter/summingbird-scalding_2.9.3 "0.1.4"]
                 [com.twitter/summingbird-batch_2.9.3 "0.1.4"]
                 [com.twitter/summingbird-builder_2.9.3 "0.1.4"]
                 [org.apache.hadoop/hadoop-core "1.1.2"]]
  :scala-source-path "src/scala"
  :prep-tasks ["scalac"]
  :uberjar-name "wordcount.jar"
  :main net.drib.sumdroppings.wordcount.WordStreamer)

