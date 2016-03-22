package com.busap

/**
 * Created by dell on 2015/5/6.
 */
import akka.actor._
import akka.actor.Props
import com.busap.cctv.handler.ForwardMessage
import com.busap.cctv.conf._
import com.busap.cctv.amqp.AMQPConnection
import com.busap.cctv.handler.AMQPListener
import com.busap.cctv.hbase.HbaseConnection
import com.busap.cctv.redis.RedisConnection
object StartListener extends App {val system = ActorSystem("listener")
  val dispachMessage = system.actorOf(Props[ForwardMessage], name = "dispachMessage")
   val listener = system.actorOf(Props(new AMQPListener(AMQPConnection.createConnection.createChannel(), Conf.queueName_media)))
  HbaseConnection.getHbaseConnection();
  RedisConnection.getReidsPool();
  print("listener is start!!!")
 }
