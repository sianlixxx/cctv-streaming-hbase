package com.busap.cctv.handler

import akka.actor.{ActorLogging, Actor}
import com.busap.cctv.listener.StationMedia

/**
 * Created by dell on 2015/5/29.
 */

import com.busap.cctv.hbase.{HbaseConnection, HbaseAPI}
class StationReceive extends Actor  with Serializable  with ActorLogging {
  def receive = {
    case station:StationMedia => receiveMessage(station)
    case _ => println("received unknown message?")
  }
  /**
   * 场站业务处理actor
   */
  def  receiveMessage(station: StationMedia)={
    HbaseConnection.getHbaseConnection();
    log.info("receiveMessage:"+station.toString);
    val msg=station.appKey;
    val stationInfo = msg.split("-", 4)
    stationInfo.map((x:String)=> log.info("............busInfo data is:"+x));
      val hbaseAPI=new HbaseAPI();
    if (stationInfo.length == 4) {
      log.info("stationInfo(1) :"+stationInfo(1)+" stationInfo(3):"+stationInfo(3))
      val rowKey = StringBuilder.newBuilder
      rowKey.append(stationInfo(1)).append("-").append(station.playId).append("-")
        .append(station.state).append("-station-")
        .append((Integer.MAX_VALUE - Integer.valueOf(stationInfo(2))))
        .append("-").append(stationInfo(3))
      val time = if (station.state.equals("begin")) station.beginTime else station.endTime
      hbaseAPI.insertStation(rowKey.toString(),time);
    }else
    {
      log.warning("received  message is wrong"+msg)
    }
  }




}
