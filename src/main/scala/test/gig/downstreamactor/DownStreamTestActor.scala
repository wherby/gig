package test.gig.downstreamactor

import akka.actor.{Actor, ActorLogging}
import cakesolutions.kafka.akka.ConsumerRecords
import gig.msg.ConsumerMsg.{GigAckIndex, GigAckOffset}

import scala.collection.JavaConverters._
/**
  * For gig.GigConsumer in gig
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2018/8/20
  */
class DownStreamTestActor(ackPattern:String) extends Actor  with ActorLogging {

  def this()={
    this("noACK")
  }
  override def preStart() = log.info("Yo, start  new downstream receiver "  )
  def receive = {
    case ConsumerRecords(offsets,records) =>
      log.info(offsets.toString())
      records.partitions().asScala map{
        partition => for( ele <- records.records(partition).asScala.zipWithIndex){
          if(ele._2 < 2 || ele._2 %100 ==0){
            log.info(ele._1.value().toString)
          }
          if(ackPattern == "ACK"){
            sender()!GigAckIndex(partition,ele._2)
          }
        }
      }
      if(ackPattern == "offsetACK"){
        sender()! GigAckOffset(offsets)
      }
  }
}
