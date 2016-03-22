package com.busap.cctv.logger

/**
 * Created by dell on 2015/5/26.
 */
class AbstractLog(var playId: Long, var wifi: Boolean, var beginTime: Long, var endTime: Long, var appKey: String) extends scala.Serializable {
  var state: String = "" // 状态: 默认", 开始状态begin, 结束end
}

//object LogTypeEnum extends Enumeration{
//  type LogType = Value
//  val MEDIA = Value(1) // 媒体传输日志
//  val PLAY = Value(2) // 播放日志
//  val APP = Value(3)  // 应用传输日志
//}


/**
 * cctvLog
 * @param playId 媒体传输日志和播放日志时存放layId  　应用传输日志时存放的
 * @param wifi
 * @param beginTime
 * @param endTime
 * @param logType
 * @param appKey
 * @param mediaFileName
 */
class CctvLog(playId: Long, wifi: Boolean, beginTime: Long, endTime: Long, appKey: String,
              var logType: Byte, var mediaFileName: String) extends AbstractLog(playId, wifi, beginTime, endTime, appKey) {

  /**
   * 应用一个父类的log构造一个子类的log
   * @param logType
   * @param mediaFileName
   * @param log
   */
  def this(logType: Byte, mediaFileName: String, log: AbstractLog) {
    this(log.playId, log.wifi, log.beginTime, log.endTime, log.appKey, logType, mediaFileName)
  }
}

