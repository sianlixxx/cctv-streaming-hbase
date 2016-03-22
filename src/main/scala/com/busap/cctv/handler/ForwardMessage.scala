package com.busap.cctv.handler

import akka.actor.SupervisorStrategy.{Escalate, Stop, Restart, Resume}
import akka.actor._
import com.busap.cctv.exception.BusinessException
import com.google.common.base.Throwables

import com.google.gson.Gson
import com.rabbitmq.client.Channel

/**
 * Created by dell on 2015/5/28.
 */
import akka.actor.OneForOneStrategy
import scala.concurrent.duration._
import com.busap.cctv.logger.MessageGenerator.{ConsumerMessage}
import akka.actor.{Props}
import com.busap.cctv.listener.ExecuteMessage
class ForwardMessage extends Actor with ActorLogging {
  var load: ActorRef = _

  override def preStart() {
   // self ! "init"
    load = context.actorOf(Props[ExecuteMessage], "excuterMessage")
    context.watch(load)
  }

  override val supervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute) {
      case _: ArithmeticException => Resume
      case _: NullPointerException => Restart
      case _: IllegalArgumentException => Stop
      case _: Exception => Escalate
      case _: BusinessException => Restart
    }

  def receive = {
    case ConsumerMessage(s) => load ! s;
    case Terminated(terminatedActorRef)=>{
      log.error(s"Child Actor {$terminatedActorRef} Terminated")
    }
    case _ => println("received unknown message?")
  }


}
