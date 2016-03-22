package com.busap.cctv.amqp

import java.util.concurrent.TimeUnit

import com.rabbitmq.client.{ConnectionFactory, Connection}
import org.springframework.retry.backoff.FixedBackOffPolicy
import org.springframework.retry.policy.AlwaysRetryPolicy
import org.springframework.retry.support.RetryTemplate
import org.springframework.retry.{RetryContext, RetryCallback}

import com.busap.cctv.conf.Conf
/**
 * Created by dell on 2015/6/5.
 */

object AMQPConnection  extends Serializable {
  val connection: Connection = null

  /**
   * Return a connection if one doesn't exist. Else create
   * a new one
   */
  def getConnection(): ConnectionFactory = {
    val factory = new ConnectionFactory()
    factory.setHost(Conf.ampq_host)
    factory.setUsername(Conf.ampq_username);
    factory.setPassword(Conf.amqp_password);
 //   factory.setVirtualHost(Conf.amqp_vhost);
    factory.setPort(Conf.amqp_port)
    factory.setRequestedHeartbeat(30)
    factory.setConnectionTimeout(60)
    factory
  }

  /**
   * 创建链接
   * @return
   */
  def createConnection: Connection = {
    var connection: Connection = null
    try {
      connection match {
        case null => {
          connection = getConnection.newConnection()
        }

        case _ => connection;
      }
    } catch {
      case e: Exception => {
        connection = loadRetryTemplate.execute(new RetryCallback[Connection, Exception] {
          override def doWithRetry(context: RetryContext): Connection = {
            getConnection().newConnection
          }
        })
        println(e.printStackTrace)
      }
    }
    connection
  }


  /**
   * 装载永久重试模板
   * @return
   */
  def loadRetryTemplate: RetryTemplate = {
    val retryTemplate = new RetryTemplate
    //重试策略为永远重试
    retryTemplate.setRetryPolicy(new AlwaysRetryPolicy)
    //固定时间间隔返回策略（1分钟）
    val backOffPolicy = new FixedBackOffPolicy
    backOffPolicy.setBackOffPeriod(TimeUnit.MILLISECONDS.convert(60, TimeUnit.SECONDS))
    //设置
    retryTemplate.setBackOffPolicy(backOffPolicy)
    retryTemplate
  }

}