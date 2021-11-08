package com.gnm.interview.sriram

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

import java.util.Properties
import scala.util.Random

/**
 * StockTickerProducer produces continuous stream of stock ticker price data
 * and publishes it to kafka
 * @param kafkaProperties
 */
class StockTickerProducer(topicName: String, messagesPerSec: Int, priceVariance: Int, kafkaProperties: Properties) {

  private val kafkaProducer = new KafkaProducer[Nothing, Message](kafkaProperties)
  private val baseTickers = Seq(
    Ticker("AMZN", 1902),
    Ticker("MSFT", 107),
    Ticker("AAPL", 215),
  )
  private val rng = new Random

  assert(priceVariance >= 0)

  /**
   * Generates a ticker price with -/+10 % variance
   * @param basePrice The price off of which we calculate variance
   * @return
   */
  def generateTickerPrice(basePrice: Int): Int = {
    val variancePercentage = rng.between(-priceVariance, priceVariance + 1)
    basePrice + ((basePrice * variancePercentage) / 100)
  }

  def generateMessage(): Message = {
    // Randomly choose 1 to 3 tickers to be sent in the message
    val numberOfStocks = rng.between(1, baseTickers.size + 1)
    val shuffledTickers = rng.shuffle(baseTickers)
    val chosenTickers = shuffledTickers.slice(0, numberOfStocks)
    // For the chosen tickers generate new price for this message
    val newTickersWithPrice = chosenTickers.map(ticker => {
      val tickerPrice = generateTickerPrice(ticker.price)
      Ticker(ticker.name, tickerPrice)
    })

    Message(newTickersWithPrice)
  }

  def start(): Unit = {
    println("Starting Producer...")
    var counter = 0
    while(true) {
      val newMessage = generateMessage()
      val record = new ProducerRecord[Nothing, Message](topicName, newMessage)
      kafkaProducer.send(record)
      counter += 1
      if (counter % 100 == 0) {
        println(s"Generated and Published $counter records")
      }
      // When messagesPerSec = 10, waits 100ms before each message
      Thread.sleep(1000 / messagesPerSec)
    }
  }
}
