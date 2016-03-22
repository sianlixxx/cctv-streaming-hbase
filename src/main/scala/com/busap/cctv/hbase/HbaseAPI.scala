package com.busap.cctv.hbase

/**
 * Created by dell on 2015/5/27.
 */


import java.util.concurrent.Executors

import com.busap.cctv.conf.Conf
import org.apache.commons.io.IOUtils
import org.apache.hadoop.hbase.client._
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.hbase.{ HBaseConfiguration}
import org.apache.spark.{Logging}

class HbaseAPI extends  Serializable with  Logging{

  /**
   * 添加日志数据到station
   * @param key
   * @param time
   */
  def insertStation(key:String,time:Long)={
   val connection= HbaseConnection.getHbaseConnection();
    val table = connection.getTable(Conf.station_download_log);
    val put = new Put(Bytes.toBytes(key.toString()), time)
    put.add(Bytes.toBytes("d"), Bytes.toBytes("time"), Bytes.toBytes(time.toString()))
    table.put(put)
    IOUtils.closeQuietly(table)
    logDebug("save busline statistics")
  }

  /**
   *  areaId-playId-begin-terminal-(Integer.MAX_VALUE-companyId)-(Integer.MAX_VALUE -lineId)-appkey
   * @param key
   * @param time
   */
  def insertTerminal(key:String,time:Long)={
    val connection= HbaseConnection.getHbaseConnection();
    val table = connection.getTable(Conf.terminal_download_log);
    val put = new Put(Bytes.toBytes(key.toString()), time)
    put.add(Bytes.toBytes("d"), Bytes.toBytes("time"), Bytes.toBytes(time.toString()))
    table.put(put)
    IOUtils.closeQuietly(table)
    logDebug("save busline statistics")
  }

  /**
   *  areaId-playId-(Integer.MAX_VALUE-companyId)-(Integer.MAX_VALUE-lineId)
   * @param key
   * @param time
   * @return
   */
  def insertBusline(key:String,time:Long)={
    val connection= HbaseConnection.getHbaseConnection();
    val table = connection.getTable(Conf.busline_download_log)
    table.incrementColumnValue(Bytes.toBytes(key), Bytes.toBytes("d"), Bytes.toBytes("c"), 1L)
    table.flushCommits()
    IOUtils.closeQuietly(table)
    logDebug("save busline Busline")
  }



}
object HbaseConnection{
  var connection:HConnection = null
  def getHbaseConnection() :HConnection= {
    val config = HBaseConfiguration.create()
    config.set("hbase.zookeeper.quorum", Conf.hbase_quorum)
    try {
      connection match{
        case null => {
          connection = HConnectionManager.createConnection(config, Executors.newSingleThreadExecutor())
        }
        case _ => connection;
      }
    } catch {
      case e: Exception => {println(e.printStackTrace)
      }
    }
    connection
  }
}
