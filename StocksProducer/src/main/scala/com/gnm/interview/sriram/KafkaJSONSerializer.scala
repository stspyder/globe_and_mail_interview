package com.gnm.interview.sriram

import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule

import org.apache.kafka.common.serialization.Serializer
import org.apache.kafka.common.errors.SerializationException

/**
 * JSON Serializer that uses Jackson's Object Mapper to
 * serialize objects to JSON
 * @tparam T Type of the data being serialized
 */
class KafkaJSONSerializer[T] extends Serializer[T] {

  private val mapper = JsonMapper.builder()
    .addModule(DefaultScalaModule)
    .build()

  override def serialize(topic: String, data: T): Array[Byte] = {
    if (data == null) {
      return null
    }

    try {
      mapper.writeValueAsBytes(data)
    } catch {
      case e: Exception => throw new SerializationException("Error serializing JSON message", e)
    }
  }
}
