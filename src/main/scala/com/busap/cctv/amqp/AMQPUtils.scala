package com.busap.cctv.amqp

import com.rabbitmq.client.Channel
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.dstream.ReceiverInputDStream

/**
 * Created by dell on 2015/5/29.
 */
object AMQPUtils {

  /**
   * 创建流
   * @param ssc
   * @param storageLevel
   * @return
   */
  def createStream( ssc: StreamingContext,
                    storageLevel: StorageLevel = StorageLevel.MEMORY_AND_DISK_SER_2): ReceiverInputDStream[String] = {
    new AMQPInputDStream(ssc, storageLevel)
  }

}