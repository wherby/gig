package gig.model

import cakesolutions.kafka.akka.Offsets
import org.apache.kafka.common.TopicPartition
import org.slf4j.LoggerFactory

class MsgRecorder {

  import scala.collection.mutable.Map

  var msgVerified: Map[TopicPartition, Map[Long, Int]] = Map()
  var msgNotVerified:Map[TopicPartition, Map[Long, Int]] = Map()
  var notVerified: Long = _
  val log= LoggerFactory.getLogger(this.getClass)

  def setupOffSets(offsets: Offsets,count: Int) {
    notVerified = count
  }

  def recordSingleAck(topic: TopicPartition, index: Long) = {
    msgVerified.get(topic) match {
      case Some(topicMap: Map[Long, Int]) => topicMap += (index -> 1)
      case None => msgVerified += (topic -> Map(index -> 1))
    }
    notVerified = notVerified - 1
    notVerified <= 0
  }

  def recordSingleFailedAck(topic: TopicPartition, index: Long) = {
    msgNotVerified.get(topic) match {
      case Some(topicMap: Map[Long, Int]) => topicMap += (index -> 1)
      case None => msgNotVerified += (topic -> Map(index -> 1))
    }
    notVerified = notVerified - 1
    notVerified <= 0
  }
}
