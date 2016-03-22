package com.busap.cctv.conf

import com.typesafe.config.ConfigFactory

/**
 * Created by jecc on 2015/6/5.
 */
class  Conf  extends Serializable {

}
object Conf {
  val config = ConfigFactory.load("application")
  val host = config.atPath("resources/application.properties")
  /**
   * rabbit mq properties
   */
  val ampq_host: String = config.getString("amqp.host")
  val ampq_username: String = config.getString("amqp.uname")
  val amqp_password: String = config.getString("amqp.pwd")
  val amqp_vhost:String=config.getString("amqp.vhost");
  val amqp_port:Int=config.getString("amqp.port").toInt;

  val exchangName: String = config.getString("amqp.exchangeName")
  val exchangeType: String = config.getString("amqp.exchangeType")

  val queueName_media: String = config.getString("amqp.queueName.media")
  val queueName_play: String = config.getString("amqp.queueName.play")
  val queueName_app: String = config.getString("amqp.queueName.app")
  val queueName_schedule: String = config.getString("amqp.queueName.schedule")

  val media_key: String = config.getString("amqp.bindingKey.media")
  val play_key: String = config.getString("amqp.bindingKey.play")
  val app_key: String = config.getString("amqp.bindingKey.app")
  val control_key: String = config.getString("amqp.bindingKey.control")
  val routingKey: String = config.getString("amqp.routingKey")
  /**
   * hdfs loggers temp properties
   */
  val media_longgerPaht: String = config.getString("hdfs.path.media")
  val play_loggerPath: String = config.getString("hdfs.path.play")
  val app_loggerPath: String = config.getString("hdfs.path.app")
  /**
   * spark properties
   */
  //  val spark: String = config.getString("spark")
  //  val workspace: String = config.getString("workspace")

  /**
   *hdfs config
   */
  val default_fs: String = config.getString("fs.defaultFS")
  val nameservice: String = config.getString("dfs.nameservices")
  val ha_namenode_ns1: String = config.getString("dfs.ha.namenodes.ns1")
  val namenode_nn1: String = config.getString("dfs.namenode.rpc-address.ns1.nn1")
  val namenode_nn2: String = config.getString("dfs.namenode.rpc-address.ns1.nn2")
  val dfs_provider_ns1: String = config.getString("dfs.client.failover.proxy.provider.ns1")
  val dfs_append: String = config.getString("dfs.support.append")
  /**
   * spark temporary Catalog
   */
  val media_dir: String = config.getString("checkpoint.media.dir")
  val play_dir: String = config.getString("checkpoint.play.dir")
  val app_dir: String = config.getString("checkpoint.app.dir")
  /**
   * redis database connection
   */
  val redis_host: String = config.getString("redis.host")
  val redis_port: Int = config.getString("redis.port").toInt
  val redis_timeout: Int = config.getString("redis.timeout").toInt
  val redis_expire: Int = config.getString("redis.expire").toInt
  val batch_duration: Int = config.getString("batch.duration").toInt
  /**
   * hbase connection
   */
  val hbase_quorum = config.getString("spark.cctv.hbase.zookeeper.quorum")
  val station_download_log = config.getString("station_download_log")
  val terminal_download_log = config.getString("terminal_download_log")
  val busline_download_log = config.getString("busline_download_log")
}

