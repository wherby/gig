package test.gig.gigConsumerActor

import akka.actor.{ActorSystem, Props}
import cakesolutions.kafka.{KafkaProducerRecord, KafkaTopicPartition}
import cakesolutions.kafka.akka.KafkaConsumerActor.Subscribe
import cakesolutions.kafka.testkit.KafkaServer
import gig.constent.GigConfig
import gig.consumer.Consumer
import test.gig.downstreamactor.DownStreamTestActor
import gig.producer.GigProducer
import test.gig.downstreamactor.DownStreamTestActor

import scala.util.Random

object offsetACKTest3 {
  private def randomString: String = Random.alphanumeric.take(5).mkString("")
  private def randomTopicPartition = KafkaTopicPartition(randomString, 0)

  def main(args: Array[String]): Unit = {
    lazy val config = GigConfig.load()
    lazy val kafkaPort = config.getString("gig.kafka.port")

    val localKafkaServer= new KafkaServer(kafkaPort.toInt)
    localKafkaServer.startup()
    implicit val system = ActorSystem("test")
    val downStreamTestActor = system.actorOf(Props(new  DownStreamTestActor("offsetACK")),"downstreamActor")
    val consumer= Consumer.createConsumerActor(downStreamTestActor)
    val producer = GigProducer.createProducer()
    val topicPartition = randomTopicPartition

    producer.send(KafkaProducerRecord(topicPartition.topic(), None, "valueCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC"))
    producer.flush()
    val subscription = Subscribe.AutoPartition(List(topicPartition.topic()))
    consumer.subscribe(subscription)


    producer.send(KafkaProducerRecord(topicPartition.topic(), None, "value2CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC"))
    producer.flush()

    val downStreamTestActor2 = system.actorOf(Props[DownStreamTestActor],"downstreamActor2")
    val consumer2= Consumer.createConsumer(downStreamTestActor2)
    consumer2.subscribe(subscription)

    for(i <- 0 to 500000){
      producer.send(KafkaProducerRecord(topicPartition.topic(), None, "value " + i.toString))

    }
    producer.flush()

    Thread.sleep(60000)

    //localKafkaServer.close()
  }
}

