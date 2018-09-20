package gig.consumer

import akka.actor.{Actor, ActorLogging}
import cakesolutions.kafka.akka.{ConsumerRecords, Offsets}
import gig.model.MsgRecorder
import gig.msg.ConsumerMsg.{GigAckIndex, GigAckOffset, GigFailedIndex, GigFailedOffset}
import gig.msg.ControlMsg.{RecorderFinish, TimeOutMsg}
import org.apache.kafka.clients.consumer
import org.apache.kafka.common.TopicPartition

import scala.collection.JavaConverters._

/**
  * For gig.consumer in gig
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2018/9/1
  */
class RecorderActor extends Actor with ActorLogging {
  val recorder: MsgRecorder = new MsgRecorder()
  var offsetsRecord: Offsets = _
  var consumerRecords: consumer.ConsumerRecords[Any, Any] = _

  def receive = {
    case ConsumerRecords(offsets, records) =>
      recorder.setupOffSets(offsets, records.count())
      offsetsRecord = offsets
      consumerRecords = records
    case GigAckIndex(topic, index) =>
      if (recorder.recordSingleAck(topic, index)) {
        recordResultToKafka(consumerRecords, checkIndexRecorded)
      }
    case GigFailedIndex(topic, index) =>
      if (recorder.recordSingleFailedAck(topic, index)) {
        recordResultToKafka(consumerRecords, checkIndexRecorded)
      }
    case TimeOutMsg(_) => {
      recordResultToKafka(consumerRecords, checkIndexRecorded)
    }
    case GigAckOffset(offset) => {
      if (offset == offsetsRecord) recordResultToKafka(consumerRecords, checkIndexRecorded)
    }
    case GigFailedOffset(offset) => {
      if (offset == offsetsRecord) recordResultToKafka(consumerRecords, (_, _) => false)
    }
  }

  private def checkIndexRecorded(topic: TopicPartition, index: Long): Boolean = {
    recorder.msgVerified.get(topic) map {
      recordMap =>
        recordMap.get(index) map {
          _ => return true
        }
    }
    false
  }

  private def recordResultToKafka(consumerRecords: consumer.ConsumerRecords[Any, Any], isIndexRecordAcK: (TopicPartition, Long) => Boolean): Unit = {
    log.debug(s"start result Logging : $offsetsRecord")
    consumerRecords.partitions().asScala map {
      topic =>
        consumerRecords.records(topic).asScala.zipWithIndex.map {
          record =>
            if (isIndexRecordAcK(topic, record._2.toLong)) {
              log.debug(s"send record to processed Topic: ${record._1}")
            } else {
              log.debug(s"send record to not processed Topic ${record._1}")
            }
        }
    }
    sender() ! RecorderFinish(offsetsRecord)
  }
}
