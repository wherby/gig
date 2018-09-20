package test.gig.gigConsumerActor

import akka.actor.{ActorSystem, Props}
import cakesolutions.kafka.{KafkaProducerRecord, KafkaTopicPartition}
import cakesolutions.kafka.akka.KafkaConsumerActor.Subscribe
import cakesolutions.kafka.testkit.KafkaServer
import gig.constent.GigConfig
import gig.consumer.Consumer
import gig.producer.GigProducer
import test.gig.downstreamactor.DownStreamTestActor

import scala.util.Random

object offsetACKTest5 {
  private def randomString: String = Random.alphanumeric.take(5).mkString("")
  private def randomTopicPartition = KafkaTopicPartition(randomString, 0)

  def main(args: Array[String]): Unit = {
    val config = GigConfig.load()
    val kafkaPort = config.getString("gig.kafka.port")

    val localKafkaServer= new KafkaServer(kafkaPort.toInt)
    localKafkaServer.startup()
    implicit val system = ActorSystem("test")
    val downStreamTestActor = system.actorOf(Props(new  DownStreamTestActor("offsetACK")),"downstreamActor")
    val consumer= Consumer.createConsumerActor(downStreamTestActor,Map("max.poll.records"->Int.box(5000)))
    val producer = GigProducer.createProducer()
    val topicPartition = randomTopicPartition

    producer.send(KafkaProducerRecord(topicPartition.topic(), None, "valueCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC"))
    producer.flush()
    for(i <- 0 to 500000){
      producer.send(KafkaProducerRecord(topicPartition.topic(), None, "value " + i.toString))
    }
    producer.flush()
    val subscription = Subscribe.AutoPartition(List(topicPartition.topic()))
    consumer.subscribe(subscription)


    producer.send(KafkaProducerRecord(topicPartition.topic(), None, "value2CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC"))
    producer.flush()

    val downStreamTestActor2 = system.actorOf(Props[DownStreamTestActor],"downstreamActor2")
    val consumer2= Consumer.createConsumerActor(downStreamTestActor2,Map("max.poll.records"->Int.box(5000)))
    consumer2.subscribe(subscription)



    Thread.sleep(60000)

    //localKafkaServer.close()
  }
}
