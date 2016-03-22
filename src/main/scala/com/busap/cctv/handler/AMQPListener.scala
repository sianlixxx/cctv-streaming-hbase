package com.busap.cctv.handler

import akka.actor.{ActorSystem, Props, Actor}
import com.busap.cctv.logger.CctvLog
import com.google.gson.Gson
import com.rabbitmq.client.{ QueueingConsumer, Channel}


/**
 * Created by jecc on 2015/5/6.
 */
import  akka.actor.ActorLogging
import com.busap.cctv.conf.Conf
import org.apache.spark.Logging
import com.busap.cctv.logger.MessageGenerator.{ConsumerMessage,MediaJson}

class AMQPListener(channel: Channel, queue: String) extends Actor with   ActorLogging {

  override def preStart = {
    self ! Start
    log.info("AMQPListener is start");
  }

  def receive  = {
    case _ => startReceving
  }

  def startReceving : Unit = {
    val system = ActorSystem("listener")
    val dispachMessage = system.actorOf(Props[ForwardMessage], name = "dispachMessage")
    channel.exchangeDeclare(Conf.exchangName, Conf.exchangeType,true);
    channel.queueDeclare(queue, true, false, false, null)
    channel.queueBind(queue,Conf.exchangName,Conf.media_key)
    val consumer = new QueueingConsumer(channel)
    channel.basicConsume(queue, true, consumer)
    while (true) {
      val delivery = consumer.nextDelivery()
      val msg = new String(delivery.getBody())
      log.debug("consumer is message"+msg)
      val cctvlog = new Gson().fromJson(msg, classOf[CctvLog])
     dispachMessage!ConsumerMessage(MediaJson(dispachMessage,cctvlog));
    }
  }
}


class AMQPPublisher(channel: Channel,exchange: String, routingKey: String) extends Actor with Serializable  with Logging {
  def receive = {
    case msg: String => channel.basicPublish(exchange, routingKey, null, msg.getBytes())
  }
}

case  class Start();