package com.busap.cctv.handler

import com.busap.cctv.listener.BusMedia
import com.busap.cctv.hbase.{HbaseConnection, HbaseAPI}
import akka.actor.{OneForOneStrategy, ActorLogging, Actor}
import akka.actor.SupervisorStrategy.{Escalate, Stop, Restart, Resume}
import scala.concurrent.duration._
/**
 * Created by dell on 2015/5/29.
 */
class BusReceive extends Actor  with Serializable  with ActorLogging {

  //val childActor = context.actorOf(Props[WorkerActor], name = "workerActor")
  override val supervisorStrategy = OneForOneStrategy( maxNrOfRetries = 10, withinTimeRange = 10 seconds) {
    case _: ArithmeticException => Resume
    case _: NullPointerException => Restart
    case _: IllegalArgumentException => Stop
    case _: Exception => Escalate
  }
  def receive = {
    case busMedia:BusMedia => receiveMessage(busMedia)
    case _ => println("received unknown message?")
  }

  /**
   * 场站业务处理actor
   */
  def  receiveMessage(busMedia: BusMedia)={
    HbaseConnection.getHbaseConnection();
    log.info("receiveMessage:"+busMedia.toString);
    val msg=busMedia.appKey;
    val busInfo = msg.split("-", 5)
    busInfo.map((x:String)=> log.info("............busInfo data is:"+x));
    val hbaseAPI=new HbaseAPI();
    if (busInfo.length == 5) {
      val rowKey = StringBuilder.newBuilder
      rowKey.append(busInfo(1)).append("-").append(busMedia.playId).append("-")
        .append(busMedia.state).append("-terminal-")
        .append((Integer.MAX_VALUE - Integer.valueOf(busInfo(2)))).append("-")
        .append(Integer.MAX_VALUE - Integer.valueOf(busInfo(3)))
        .append("-").append(busInfo(4))
      val time = if (busMedia.state.equals("begin")) busMedia.beginTime else busMedia.endTime
      hbaseAPI.insertStation(rowKey.toString(),time);
    }else
    {
      log.info("received  message is wrong"+msg)
    }
  }

}
