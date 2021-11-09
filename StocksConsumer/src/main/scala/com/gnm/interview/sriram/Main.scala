package com.gnm.interview.sriram

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.{col, current_timestamp, explode, from_json, lit, window}
import org.apache.spark.sql.streaming.Trigger.ProcessingTime
import org.apache.spark.sql.types.{DataTypes, IntegerType, StringType, StructType}

import java.time.LocalDateTime

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
    val topicName = checkAndGetEnvVariable(Constants.ENV_CONSUMER_KAFKA_TOPIC_NAME)
    val triggerInterval = checkAndGetEnvVariable(Constants.ENV_CONSUMER_TRIGGER_INTERVAL_SECS)
    val consumerStartingOffsetSetting = checkAndGetEnvVariable(Constants.ENV_CONSUMER_STARTING_OFFSETS)

    val spark = SparkSession.builder()
      .appName("Stocks Consumer")
      .master("local")
      .getOrCreate()

    spark.sparkContext.setLogLevel("WARN")

    val schema = new StructType()
      .add("eventTime", DataTypes.TimestampType)
      .add("tickers", DataTypes.createArrayType(
        new StructType()
          .add("name", StringType)
          .add("price", IntegerType)
      ))

    val df = spark.readStream.format("kafka")
      .option("startingOffsets", consumerStartingOffsetSetting)
      .option("kafka.bootstrap.servers", bootstrapServers)
      .option("subscribe", topicName)
      .load()

    df.select(from_json(col("value").cast(StringType), schema).as("data"))
      .select(col("data.eventTime"), explode(col("data.tickers")).as("ticker"))
      .select(col("eventTime"), col("ticker.name").as("ticker"), col("ticker.price").as("price"))
      .withWatermark("eventTime", "1 minutes")
      .groupBy(window(col("eventTime"), "30 seconds"), col("ticker"))
      .avg("price")
      .select(col("window.start").as("start_time"), col("window.end").as("end_time"), col("ticker"), col("avg(price)").as("avg_price"))
      .withColumn("process_time", current_timestamp())
      .writeStream
      .format("console")
      .option("truncate", value = false)
      .outputMode("append")
      .trigger(ProcessingTime(s"$triggerInterval seconds"))
      .start()
      .awaitTermination()
  }
}
