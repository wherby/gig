package gig.consumer

import java.util.UUID

import akka.actor.{ ActorRef, ActorSystem, Props}
import cakesolutions.kafka.akka.KafkaConsumerActor
import gig.constent.{GigConfig, KafkaConfig}

/**
  * For gig in gig
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2018/8/20
  */
object Consumer {
  lazy val config = GigConfig.load()
  lazy val kafkaHost = config.getString("gig.kafka.host")
  lazy val kafkaPort = config.getString("gig.kafka.port")

  case class FanOutConf(leftActor: Option[ActorRef] = None, rightActor: Option[ActorRef] = None)

  def createConsumer(downstreamActor: ActorRef, config: Map[String, AnyRef] = Map())(implicit system: ActorSystem): KafkaConsumerActor = {
    KafkaConsumerActor(KafkaConfig.getConsumerConfig(s"$kafkaHost:$kafkaPort", overrideConfig = config),
      KafkaConsumerActor.Conf(), downstreamActor)
  }

  def createConsumerActor(downstreamActor: ActorRef, config: Map[String, AnyRef] = Map(), fanOutConf: FanOutConf = FanOutConf())(implicit system: ActorSystem): KafkaConsumerActor = {
    val gigActor = system.actorOf(Props(new ConsumerActor(downstreamActor,fanOutConf)), "gigConsumer" + UUID.randomUUID().toString)
    KafkaConsumerActor(KafkaConfig.getConsumerConfig(s"$kafkaHost:$kafkaPort", overrideConfig = config),
      KafkaConsumerActor.Conf(),
      gigActor)
  }
}
