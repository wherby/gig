package gig.msg

import akka.actor.ActorRef
import cakesolutions.kafka.akka.{ConsumerRecords, Offsets}

/**
  * For gig.model in gig
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2018/8/26
  */
object ControlMsg {

  final case class TimeOutMsg(offsets: Offsets)

  final case class GigConsumerMsg(replyTo: ActorRef, consumerRecords: ConsumerRecords[Any, Any])

  final case class RecorderFinish(offsets: Offsets)
}
