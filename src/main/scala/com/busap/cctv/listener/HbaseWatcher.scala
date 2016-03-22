package com.busap.cctv.listener

import akka.actor.{Terminated, ActorLogging, Actor}
import akka.util.Timeout
import scala.concurrent.duration._
/**
 * Created by dell on 2015/6/10.
 */
class HbaseWatcher extends Actor with ActorLogging {

  implicit val timeout = new Timeout(15 seconds)

  def receive = {
    case Terminated(terminatedActorRef)=>{
      log.error(s"Child Actor {$terminatedActorRef} Terminated")
    }
  }
}
