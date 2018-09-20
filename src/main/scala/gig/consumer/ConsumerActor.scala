package gig.consumer

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import cakesolutions.kafka.akka.ConsumerRecords
import gig.consumer.Consumer.FanOutConf
import gig.msg.ControlMsg.GigConsumerMsg

object ConsumerActor{
  def dispatcherProps(downstreamActor: ActorRef, fanOutConf: FanOutConf)={
    Props(new Dispatcher(downstreamActor,fanOutConf))
  }
}

class ConsumerActor(downstreamActor: ActorRef,fanOutConf: FanOutConf) extends Actor with ActorLogging {

  def receive = {
    case ConsumerRecords(offsets, records) =>
      handleConsumerRecords(ConsumerRecords(offsets, records))
    case msg =>
      log.debug(msg.toString)
  }

  private def handleConsumerRecords(consumerRecords: ConsumerRecords[Any, Any]) = {
    val replyTo: ActorRef = sender()
    val gigDispatcher = context.actorOf(ConsumerActor.dispatcherProps(downstreamActor,fanOutConf))
    gigDispatcher ! GigConsumerMsg(replyTo, consumerRecords)
  }
}
