package gig.msg

import cakesolutions.kafka.akka.{ Offsets}
import org.apache.kafka.common.TopicPartition


/**
  * For gig.model in gig
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2018/8/26
  */
object ConsumerMsg {

  sealed trait ConsumerAck

  final case class GigAckOffset(offset: Offsets) extends ConsumerAck

  final case class GigFailedOffset(offset: Offsets) extends ConsumerAck

  final case class GigAckIndex(topic: TopicPartition, index: Long) extends ConsumerAck

  final case class GigFailedIndex(topic: TopicPartition, index: Long) extends ConsumerAck

}
