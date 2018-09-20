package gig.constent

import cakesolutions.kafka.KafkaConsumer
import org.apache.kafka.clients.consumer.OffsetResetStrategy
import org.apache.kafka.common.serialization.StringDeserializer

/**
  * For gig.constent in gig
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2018/8/20
  */
object KafkaConfig {
  def getConsumerConfig(kafkaServer: String,
                        groupId: String = "test",
                        enableAutoCommit: Boolean = false,
                        autoOffsetReset: OffsetResetStrategy = OffsetResetStrategy.EARLIEST,
                        overrideConfig: Map[String, AnyRef] = Map()
                        ): KafkaConsumer.Conf[String, String] = {
    var kafkaConfig = KafkaConsumer.Conf(
      new StringDeserializer,
      new StringDeserializer,
      bootstrapServers = kafkaServer,
      groupId = groupId,
      enableAutoCommit = enableAutoCommit,
      autoOffsetReset = autoOffsetReset
    )
    for (item <- overrideConfig) {
      kafkaConfig = kafkaConfig.withProperty(item._1, item._2)
    }
    kafkaConfig
  }
}
