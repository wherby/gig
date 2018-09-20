package test.gig.model

import gig.model.MsgRecorder
import org.apache.kafka.common.TopicPartition

object MsgRecoderTest {
  def main(args: Array[String]): Unit = {
    val recorder: MsgRecorder = new MsgRecorder()
    println(recorder.msgVerified)
    recorder.recordSingleAck(new TopicPartition("test",0),1)
    println(recorder.msgVerified)
    recorder.recordSingleAck(new TopicPartition("test",0),2)
    println(recorder.msgVerified)
  }
}
