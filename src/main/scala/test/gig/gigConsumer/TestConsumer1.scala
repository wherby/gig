package test.gig.gigConsumer

import akka.actor.{ActorSystem, Props}
import cakesolutions.kafka.akka.KafkaConsumerActor.Subscribe
import cakesolutions.kafka.testkit.KafkaServer
import cakesolutions.kafka.{KafkaProducerRecord, KafkaTopicPartition}
import gig.consumer.Consumer
import gig.constent.GigConfig
import gig.producer.GigProducer
import test.gig.downstreamactor.DownStreamTestActor

import scala.util.Random

/**
  * For gig in gig
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2018/8/20
  */
object TestConsumer1 {
  private def randomString: String = Random.alphanumeric.take(5).mkString("")
  private def randomTopicPartition = KafkaTopicPartition(randomString, 0)

  def main(args: Array[String]): Unit = {
    lazy val config = GigConfig.load()
    lazy val kafkaPort = config.getString("gig.kafka.port")

    val localKafkaServer= new KafkaServer(kafkaPort.toInt)
    localKafkaServer.startup()
    implicit val system = ActorSystem("test")
    val downStreamTestActor = system.actorOf(Props[DownStreamTestActor],"downstreamActor")
    val consumer= Consumer.createConsumer(downStreamTestActor)
    val producer = GigProducer.createProducer()
    val topicPartition = randomTopicPartition

    producer.send(KafkaProducerRecord(topicPartition.topic(), None, "valueCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC"))
    producer.flush()
    val subscription = Subscribe.AutoPartition(List(topicPartition.topic()))
    consumer.subscribe(subscription)



    Thread.sleep(10000)
    consumer.unsubscribe()

    Thread.sleep(4000)

    producer.send(KafkaProducerRecord(topicPartition.topic(), None, "value2CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC"))
    producer.flush()

    Thread.sleep(60000)
    val downStreamTestActor2 = system.actorOf(Props[DownStreamTestActor],"downstreamActor2")
    val consumer2= Consumer.createConsumer(downStreamTestActor2)
    consumer2.subscribe(subscription)
    Thread.sleep(60000)

    //localKafkaServer.close()
  }
}
