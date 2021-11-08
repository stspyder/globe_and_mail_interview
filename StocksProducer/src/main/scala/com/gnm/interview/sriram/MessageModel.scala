package com.gnm.interview.sriram

case class Ticker(name: String, price: Int)

case class Message(tickers: Seq[Ticker])