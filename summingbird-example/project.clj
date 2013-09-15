(defproject summingbird-example "0.1.0-SNAPSHOT"
  :repositories [["conjars" "http://conjars.org/repo"]
                 ["sonatype" "http://oss.sonatype.org/content/repositories/releases"]
  		           ["twitter-repo" "http://maven.twttr.com"]]
  :plugins [[lein-scalac "0.1.0"]]
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.scala-lang/scala-library "2.9.3"]
                 [org.twitter4j/twitter4j-core "3.0.3"]
                 [com.twitter/summingbird-core_2.9.3 "0.1.2"]
                 [com.twitter/summingbird-batch_2.9.3 "0.1.2"]
                 [com.twitter/summingbird-scalding_2.9.3 "0.1.2"]
                 [com.twitter/summingbird-storm_2.9.3 "0.1.2"]
                 [com.twitter/bijection-netty_2.9.3 "0.5.3"]
                 [com.twitter/tormenta-twitter_2.9.3 "0.5.2"]
                 [com.twitter/storehaus-memcache_2.9.3 "0.5.1"]
                 [org.apache.hadoop/hadoop-core "1.1.2"]]
  :scala-source-path "src/scala"
  :prep-tasks ["scalac"]
  :main com.twitter.summingbird.example.StormRunner)

