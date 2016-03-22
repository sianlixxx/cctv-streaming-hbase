package com.busap.cctv.listener

import akka.actor.{Props, ActorSystem}
/**
 * Created by dell on 2015/5/6.
 */
import com.busap.cctv.conf.Conf
import com.busap.cctv.amqp.AMQPConnection
import akka.actor.Props
import com.busap.cctv.handler.AMQPPublisher

object Sender extends App {
  val system = ActorSystem("sender")
  val sendingChannel = AMQPConnection.createConnection.createChannel()
 // sendingChannel.exchangeDeclare(Conf.exchangName,Conf.exchangeType,true);
  sendingChannel.queueDeclare(Conf.queueName_play, true, false, false, null)
 // sendingChannel.queueBind(Conf.queueName_play,Conf.exchangName,Conf.play_key)
  val sender = system.actorOf(Props(new AMQPPublisher(sendingChannel, Conf.exchangName, Conf.play_key)))
  (1 to 10) map { i => sender ! "Hello World" }
//   sender ! "Hello World"

}