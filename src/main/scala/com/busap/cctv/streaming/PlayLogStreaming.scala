package com.busap.cctv.streaming

/**
 * Created by dell on 2015/5/26.
 */
class PlayLogStreaming {

}


import com.busap.cctv.amqp.AMQPUtils
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{SparkConf, Logging}
object PlayLogStreaming extends Logging{



  def createContext(checkPointDir: String) = {

    val conf = new SparkConf().setAppName("cctv_streaming_play_log")
    //      .setMaster(spark)
    //      .setJars(List(workspace + "/target/cctv-streaming-1.0-SNAPSHOT-jar-with-dependencies.jar"))

    val host = conf.get("spark.cctv.amqp.host")
    val userName = conf.get("spark.cctv.amqp.username")
    val passWord = conf.get("spark.cctv.amqp.password")
    val queueName = conf.get("spark.cctv.amqp.queueName.play")
    val bindingKey = conf.get("spark.cctv.amqp.bindingKey.play")
    val exchangeName = conf.get("spark.cctv.amqp.exchangeName")
    val exchangeType = conf.get("spark.cctv.amqp.exchangeType")
    val duration = conf.getInt("spark.cctv.batch.duration", 5)

    val hdfsPlayPath = conf.get("spark.cctv.hdfs.path.play")


    val ssc = new StreamingContext(conf, Seconds(duration))
    ssc.checkpoint(checkPointDir)


    val lines = AMQPUtils.createStream(ssc,StorageLevel.MEMORY_AND_DISK_SER_2)

    lines.repartition(1).saveAsTextFiles(hdfsPlayPath)
    ssc
  }

  def main(args: Array[String]) {
    if(args.length < 1){
      println("please run with program argument, fist argument is checkpoint dir.")
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
