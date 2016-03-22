package com.busap.cctv.listener

import akka.actor.{Terminated, ActorLogging, Actor}

/**
 * Created by dell on 2015/6/10.
 */
class RedisWatcher extends Actor with ActorLogging {

  def receive = {
    case Terminated(terminatedActorRef)=>{
      log.error(s"Child Actor {$terminatedActorRef} Terminated")
    }
  }
}
