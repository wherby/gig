package gig.consumer

import java.util.concurrent.TimeUnit

import akka.actor.{Actor, ActorLogging, ActorRef, Cancellable, Props}
import cakesolutions.kafka.akka.KafkaConsumerActor.Confirm
import cakesolutions.kafka.akka.{ConsumerRecords, KafkaConsumerActor, Offsets}
import gig.consumer.Consumer.FanOutConf
import gig.msg.ControlMsg.{GigConsumerMsg, RecorderFinish, TimeOutMsg}
import gig.msg.ConsumerMsg._
import org.apache.kafka.clients.consumer

import scala.concurrent.duration.{Duration, FiniteDuration}
import scala.concurrent.ExecutionContext.Implicits.global

object Dispatcher{
  def recordeProps(fanOutConf: FanOutConf)={
    Props(new RecorderActor(fanOutConf))
  }
}

class Dispatcher(downstreamActor: ActorRef,fanOutConf: FanOutConf) extends Actor with ActorLogging {

  var cancelableTimeOut :Cancellable=_
  var replyTo:ActorRef =_
  val recorderActor: ActorRef = context.actorOf(Dispatcher.recordeProps(fanOutConf),"Recorder")

  def receive = {
    case GigConsumerMsg(reply,consumerRecords)=>
      replyTo = reply
      recorderActor ! consumerRecords
      dispatchRecord(consumerRecords.offsets,consumerRecords.records)
    case ConsumerRecords(offsets,records) =>
      dispatchRecord(offsets, records)
    case timeout@TimeOutMsg(_) =>
      log.info(s"Send  out timeout message $timeout")
      recorderActor !timeout
    case consumerAck:ConsumerAck =>
      recorderActor !consumerAck
    case RecorderFinish(offsets) =>
      replyTo ! Confirm(offsets)
      if(cancelableTimeOut.isCancelled){
        cancelableTimeOut.cancel()
      }
      log.info(s"finished processed $offsets")
      context.stop(self)
    case msg=>
      log.debug(s"Receive:${msg.toString}")
  }

  private def dispatchRecord(offsets: Offsets, records: consumer.ConsumerRecords[Any, Any]) = {
    downstreamActor ! ConsumerRecords(offsets,records)
    val timeoutValue = if(KafkaConsumerActor.Conf().unconfirmedTimeout > Duration(0.5, TimeUnit.SECONDS))
    {
      KafkaConsumerActor.Conf().unconfirmedTimeout -Duration(0.5, TimeUnit.SECONDS)
    }else{
      KafkaConsumerActor.Conf().unconfirmedTimeout/2
    }
    val timeout =  Some(timeoutValue).collect { case d: FiniteDuration => d }.getOrElse(FiniteDuration(1, TimeUnit.SECONDS))
    cancelableTimeOut = context.system.scheduler.scheduleOnce(timeout){
      self ! TimeOutMsg(offsets)
    }
  }
}