package cakasolutions.kafka.akka

import cakesolutions.kafka.KafkaTopicPartition
import cakesolutions.kafka.akka.ConsumerRecords
import org.scalatest.{FlatSpecLike, Inside, Matchers}


/**
  * For cakasolutions.kafka.akka in gig
  * Created by whereby[Tao Zhou](187225577@qq.com) on 2018/8/12
  */
class ConsumerRecordSpec extends FlatSpecLike with Matchers with Inside{
  val partition = KafkaTopicPartition("sometopic", 0)
  val knownInput: ConsumerRecords[String, Int] = ConsumerRecords.fromPairs(partition, Seq(Some("foo") -> 1))
  val partiallyKnownInput: ConsumerRecords[_, _] = knownInput
  val anyInput: Any = knownInput

  "ConsumerRecords" should "match types correctly" in {
    partiallyKnownInput.hasType[ConsumerRecords[String, Int]] shouldEqual true
    partiallyKnownInput.hasType[ConsumerRecords[Int, String]] shouldEqual false
  }

  it should "cast to only correct types" in {
    val success = partiallyKnownInput.cast[ConsumerRecords[String, Int]]
    val failure = partiallyKnownInput.cast[ConsumerRecords[Int, String]]

    success shouldEqual Some(knownInput)
    failure shouldEqual None
  }

  it should "extract values correctly" in {
    val correctExt = ConsumerRecords.extractor[String, Int]
    val incorrectExt = ConsumerRecords.extractor[Int, String]

    anyInput should not matchPattern {
      case incorrectExt(_) =>
    }

    inside(anyInput) {
      case correctExt(kvs) =>
        kvs shouldEqual knownInput
    }
  }
}
