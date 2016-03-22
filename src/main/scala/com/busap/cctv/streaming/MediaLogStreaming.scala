package com.busap.cctv.streaming

import akka.actor.{Props, ActorSystem}
import com.busap.cctv.logger.MessageGenerator.{MediaJson, ConsumerMessage}

import org.apache.commons.lang3.StringUtils

import org.apache.spark.streaming.StreamingContext._
import com.google.common.base.Throwables

import com.google.gson.Gson

import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{SparkEnv, SparkConf, Logging}

/**
 * Created by dell on 2015/5/26.
 */
import com.busap.cctv.conf.{ Conf}
import com.busap.cctv.amqp.AMQPUtils
import com.busap.cctv.logger.CctvLog
import com.busap.cctv.handler.ForwardMessage
class MediaLogStreaming {

}
object MediaLogStreaming extends  Serializable with Logging{

    def createContext(checkPointDir: String) = {

      val conf = new SparkConf().setAppName("cctv_new_streaming_media_log")
        .set("spark.streaming.unpersis", "true")
        .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
        .set("spark.kryo.registrator", "com.busap.cctv.CCTVRegisstrator")


      val ssc = new StreamingContext(conf, Seconds(Conf.batch_duration))
      ssc.checkpoint(checkPointDir)
      val lines = AMQPUtils.createStream(ssc);
   //   lines.repartition(1).saveAsTextFiles(Conf.media_longgerPaht)
      logInfo("........................start MediaStreaming.................................... ")
      val resultAll = lines.map(mapFunc = x => {
        try {
          val log = new Gson().fromJson(x.toString, classOf[CctvLog])
          val system = ActorSystem("forwardMessage")
          val dispachMessage = system.actorOf(Props[ForwardMessage], name = "forwardMessage")
          logInfo("log ..........." + log)
          dispachMessage ! ConsumerMessage(MediaJson(dispachMessage,log));
          Some(log)
        } catch {
          case e: Throwable => {
            logError(Throwables.getStackTraceAsString(e))
            None
          }
        }
      }).filter(!_.isEmpty).map(_.get)

      val cacheResultAll = resultAll.cache()


      val resultByKey = cacheResultAll.map(log => (log.appKey, 1)).cache()
     //  cacheResultAll.count().print()
      val reduceResult = resultByKey.reduceByKeyAndWindow(_ + _, _ - _, Seconds(60), Seconds(5)).cache()

      reduceResult.foreachRDD(r => {
        r.collect().foreach(item => {
          if (StringUtils.isNotBlank(item._1) && item._2 > 0) {
            logInfo("key: " + item._1 + " value: " + item._2)
          }
        })
      })

 //     lines.repartition(1).saveAsTextFiles(Conf.media_longgerPaht)

      reduceResult.map(x => (null, x._2)).reduceByKey(_ + _).map(c => c._2).foreachRDD(r => {
        val collect = r.collect()
        if (collect.length > 0) {
          logInfo("mediaLogCountByMinute:" + collect(0).toString)
        } else {
          logInfo("mediaLogCountByMinute: 0")
        }
      })

      ssc
    }

  def main(args: Array[String]) {
    if(args.length < 1){
      logInfo("please run with program argument, fist argument is checkpoint dir.")
    } else {
      val checkPointDir = args(0).toString
      val ssc = StreamingContext.getOrCreate(checkPointDir,
        () => {
          createContext(checkPointDir)
        })
      ssc.start()
      ssc.awaitTermination()
    }
  }


}
