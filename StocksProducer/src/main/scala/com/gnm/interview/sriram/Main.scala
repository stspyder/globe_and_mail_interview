package com.gnm.interview.sriram

import org.apache.kafka.clients.producer.ProducerConfig

import java.util.Properties

object Main {

  def checkAndGetEnvVariable(name: String): String = {
    val value = sys.env.get(name)
    if (value.isEmpty) {
      throw new IllegalArgumentException(s"Env Variable $name is required")
    }

    value.get
  }

  def main(args: Array[String]): Unit = {
    val bootstrapServers = checkAndGetEnvVariable(Constants.ENV_KAFKA_BOOTSTRAP_SERVERS)
    val clientID = checkAndGetEnvVariable(Constants.ENV_KAFKA_CLIENT_ID)
    val topicName = checkAndGetEnvVariable(Constants.ENV_PRODUCER_KAFKA_TOPIC_NAME)
    val messagesPerSec = checkAndGetEnvVariable(Constants.ENV_PRODUCER_MESSAGES_PER_SEC)
    val priceVariance = checkAndGetEnvVariable(Constants.ENV_PRODUCER_PRICE_VARIANCE)


    val kafkaProperties = new Properties()
    kafkaProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
    kafkaProperties.put(ProducerConfig.CLIENT_ID_CONFIG, clientID)
    kafkaProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.VoidSerializer")
    kafkaProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "com.gnm.interview.sriram.KafkaJSONSerializer")

    val producer = new StockTickerProducer(topicName, messagesPerSec.toInt, priceVariance.toInt, kafkaProperties)
    producer.start()
  }
}
