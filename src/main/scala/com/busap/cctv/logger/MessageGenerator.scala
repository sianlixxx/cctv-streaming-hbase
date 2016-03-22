package com.busap.cctv.logger

import akka.actor.ActorRef


/**
 * Created by dell on 2015/6/5.
 */
object MessageGenerator extends  Serializable{

  case class ConsumerMessage(mediaJson:MediaJson)

  case class MediaJson(target: ActorRef,msg: CctvLog);
}
