package gig.consumer

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import cakesolutions.kafka.akka.{ConsumerRecords}
import gig.msg.ControlMsg.GigConsumerMsg

class ConsumerActor(downstreamActor: ActorRef) extends Actor with ActorLogging {

  def receive = {
    case ConsumerRecords(offsets, records) =>
      handleConsumerRecords(ConsumerRecords(offsets, records))
    case msg =>
      log.debug(msg.toString)
  }

  private def handleConsumerRecords(consumerRecords: ConsumerRecords[Any, Any]) = {
    val replyTo: ActorRef = sender()
    val gigDispatcher = context.actorOf(Props(new Dispatcher(downstreamActor)))
    gigDispatcher ! GigConsumerMsg(replyTo, consumerRecords)
  }
}
