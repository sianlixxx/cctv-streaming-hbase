package com.busap.cctv.handler


import akka.actor._
import com.busap.cctv.logger.MessageGenerator.MediaJson
import com.busap.cctv.exception.BusinessException
/**
 * Created by dell on 2015/6/6.
 */
class MessageGeneratorActor extends Actor  with ActorLogging  {
  override def preStart() {
    log.info("MessageGeneratorActor restart!!! ")
  }
  def receive = {
    case msg:String => throw new BusinessException("Simulating a dummy exception after 3 requests")
    case MediaJson(actor,msg)=>{log.info("MessageGeneratorActor receive:"+msg);
      throw new BusinessException("Simulating a dummy exception after 3 requests")
    }
  }
}
