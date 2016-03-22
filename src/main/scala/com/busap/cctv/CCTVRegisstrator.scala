package com.busap.cctv


import com.busap.cctv.streaming.MediaLogStreaming;
import com.busap.cctv.streaming.PlayLogStreaming;
import com.busap.cctv.streaming.AppLogStreaming;
import com.esotericsoftware.kryo.Kryo
import com.busap.cctv.logger.CctvLog

/**
 * Created by dell on 2015/5/26.
 */
class CCTVRegisstrator extends org.apache.spark.serializer.KryoRegistrator{
  override def registerClasses(kryo: Kryo): Unit = {
    kryo.register(classOf[CctvLog])
    kryo.register(classOf[MediaLogStreaming])
    kryo.register(classOf[PlayLogStreaming])
    kryo.register(classOf[AppLogStreaming])

  }
}
