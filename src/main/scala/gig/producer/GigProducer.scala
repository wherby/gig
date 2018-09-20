package gig.producer

import cakesolutions.kafka.KafkaProducer
import gig.constent.{GigConfig}
import org.apache.kafka.common.serialization.StringSerializer

/**
  * For gig.Consumer in gig
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2018/8/20
  */
object GigProducer {
  lazy val config = GigConfig.load()
  lazy val kafkaHost = config.getString("gig.kafka.host")
  lazy val kafkaPort = config.getString("gig.kafka.port")

  def createProducer(bootstrapServerConf: Option[String] = None) = {
    val bootstrapServers = bootstrapServerConf match {
      case Some(server) => server
      case _=> kafkaHost + ":" + kafkaPort
    }
    KafkaProducer(KafkaProducer.Conf(new StringSerializer(), new StringSerializer(), bootstrapServers = bootstrapServers))
  }
}
