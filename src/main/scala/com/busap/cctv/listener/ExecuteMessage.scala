package com.busap.cctv.listener

import akka.actor._
import com.busap.cctv.amqp.AMQPConnection
import com.busap.cctv.conf.Conf
import com.busap.cctv.handler.{AMQPPublisher, BusReceive, StationReceive}
import com.busap.cctv.logger.CctvLog
import com.busap.cctv.logger.MessageGenerator.MediaJson
import com.busap.cctv.redis.{RedisAPI, RedisConnection}
import com.google.common.base.Throwables
import com.google.gson.Gson
import com.rabbitmq.client.Channel

/**
 * Created by dell on 2015/6/10.
 */
trait DispatchMessage extends Actor with ActorLogging {

  override  def receive = {
    case MediaJson(target,msg)=>mediaJson(target,msg);
    case msg:String   => log warning s"unkown: $msg"
  }
  def mediaJson(target: ActorRef,message: CctvLog)

}


class ExecuteMessage extends DispatchMessage {
  /**
   * 分发数据
   */
  override  def mediaJson(target: ActorRef,cctvlog: CctvLog){
    log.info("recevingSparkMessage:"+cctvlog);
    try {
      RedisConnection.getReidsPool();
     // val cctvlog = new Gson().fromJson(msg, classOf[CctvLog])
      val redisAPI = new RedisAPI();
      val key = cctvlog.appKey;
      val playId = cctvlog.playId;
      val mediaSetKey = "mediaSet-" + key + "-play-" + playId
      val mediaSetKeyCountKey = "mediaSet-" + key + "-play-" + playId + "-count"

      val mediaSetKeyCount = redisAPI.incr(mediaSetKeyCountKey)
      val isFirst = mediaSetKeyCount == 1
      redisAPI.sadd(mediaSetKey, cctvlog.mediaFileName)
      if (isFirst) {
        cctvlog.state = "begin"
        redisAPI.setKeyExpire(mediaSetKey)
        redisBusiness(key, 0L, cctvlog.endTime,cctvlog,redisAPI)
      } else {
        //play-$playId为redis-sync放入的数据，mediaSetKey为本应用放入的数据
        val diff = redisAPI.sdiff("play-" + playId, mediaSetKey)
        if (diff.size() == 0) {
          cctvlog.state = "end"
          redisBusiness(key, 0L, cctvlog.endTime,cctvlog,redisAPI)
          redisAPI.delKey(mediaSetKey)
          redisAPI.delKey(mediaSetKeyCountKey)
        }else
        {
          cctvlog.state = "ing"
        }
      }
      log.info("state: " + cctvlog.state)
    }catch {
      case e: Throwable => {
        log.error(Throwables.getStackTraceAsString(e))
      }
    }
  }

  /**
   *redis业务处理类
   */
  def redisBusiness(key: String, beginTime: Long, endTime: Long,cctvlog:CctvLog,redisAPI: RedisAPI):Unit= {
    val keys = redisAPI.getKeys("*-*-*-" + key)
    if (keys.size() > 0) {
      val redisKey = keys.iterator().next()
      if (redisKey != null && redisKey.length > 0 && redisKey.startsWith("station-")) {
        val system = ActorSystem("StationReceive")
        val send = system.actorOf(Props(new StationReceive()))
        send ! new StationMedia(cctvlog.playId, beginTime, endTime, redisKey, cctvlog.state);
        if(cctvlog.state.equals("end")) {
          val sendingChannel = AMQPConnection.createConnection.createChannel()
          sendScheduler(sendingChannel, cctvlog.appKey)
        }
      } else {
        if(redisKey.startsWith("bus-"))
        {
          val system = ActorSystem("BusReceive")
          val send = system.actorOf(Props(new BusReceive()))
          send ! new BusMedia(cctvlog.playId, beginTime, endTime, redisKey, cctvlog.state);
          if(cctvlog.state.equals("end")) {
            val sendingChannel = AMQPConnection.createConnection.createChannel()
            sendScheduler(sendingChannel, cctvlog.appKey)
          }
        }else
        {
          log.warning("this key is wronging, not is station key" + redisKey)
        }
      }
    } else {
      log.warning("not find key is from redis :"+key)
    }
  }

  /**
   * 通知 scheduler appkey 传输消息完毕
   */
  def sendScheduler(sendingChannel:Channel,sendAppkey:String): Unit =
  {
    sendingChannel.exchangeDeclare(Conf.exchangName,Conf.exchangeType,true);
    sendingChannel.queueDeclare(Conf.queueName_schedule, true, false, false, null)
    sendingChannel.queueBind(Conf.queueName_schedule, Conf.exchangName, Conf.routingKey)
    val system = ActorSystem("AMQPPublisher")
    val sender = system.actorOf(Props(new AMQPPublisher(sendingChannel, Conf.exchangName, Conf.routingKey)))
    sender ! sendAppkey
    //  sendingChannel.close();
    log.warning("send complete appKey: " + sendAppkey)
  }
}


class StationMedia(splayId: Long, sbeginTime: Long, sendTime: Long, sappKey: String, sstate: String)extends scala.Serializable {
  var playId: Long = splayId
  var beginTime: Long = sbeginTime
  var endTime: Long = sendTime
  var appKey: String = sappKey
  var state: String = sstate
}

class BusMedia(splayId: Long, sbeginTime: Long, sendTime: Long, sappKey: String, sstate: String) extends scala.Serializable {
  var playId: Long = splayId
  var beginTime: Long = sbeginTime
  var endTime: Long = sendTime
  var appKey: String = sappKey
  var state: String = sstate
}