package com.busap.cctv.amqp



import com.busap.cctv.conf.{Conf}

import com.rabbitmq.client.{QueueingConsumer}
import org.apache.spark.Logging
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.dstream.ReceiverInputDStream
import org.apache.spark.streaming.receiver.Receiver

/**
 * Created by dell on 2015/5/29.
 */

class AMQPInputDStream( @transient ssc_ : StreamingContext,
                         storageLevel: StorageLevel) extends ReceiverInputDStream[String](ssc_) with Logging {
  def getReceiver(): Receiver[String] = {
    new AMQPReceiver(Conf.queueName_media,storageLevel);
  }

}
class AMQPReceiver(queue: String,storageLevel: StorageLevel) extends Receiver[String](storageLevel) with Logging {

  /**
   * 接收消息
   */
  def receive: Unit = {
    val channel= AMQPConnection.createConnection.createChannel()
    channel.exchangeDeclare(Conf.exchangName, Conf.exchangeType,true);
    channel.queueDeclare(queue, true, false, false, null)
    channel.queueBind(queue,Conf.exchangName,Conf.media_key)
    val consumer = new QueueingConsumer(channel)
    channel.basicConsume(queue, true, consumer)
    while (true) {
      val delivery = consumer.nextDelivery()
      val msg = new String(delivery.getBody())
      log.info("ampqlistener is message"+msg)
      store(msg)
    }
  }

  override def onStart(): Unit = {
    logInfo("execute onStart")
    this.receive
  }

  override def onStop(): Unit = {}
}

