package com.busap.cctv.redis

/**
 * Created by dell on 2015/5/26.
 */

import java.util.Set
import com.busap.cctv.conf.Conf
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Jedis
object RedisConnection{
  private var pool:JedisPool= null;

  def getReidsPool():JedisPool={
    val config = new JedisPoolConfig();
    config.setMaxIdle(5);
    config.setTestOnBorrow(true);
    pool = new JedisPool(config,Conf.redis_host,Conf.redis_port,Conf.redis_timeout);
    pool;
  }

  /**
   * 返还到连接池
   *
   * @param pool
   * @param redis
   */
  def   returnResource( pool:JedisPool, redis:Jedis)={
    if (redis != null) {
      pool.returnResource(redis);
    }
  }
}
import org.apache.spark.{Logging}
class RedisAPI extends  Serializable  with Logging{



  /**
   * 获取String数据
   * @param key
   * @return
   */
  def  getKey(key:String):String={
    var value:String = null;
    var jedis:Jedis=null;
    val pool = RedisConnection.getReidsPool();
    try {
      jedis = pool.getResource();
      jedis.select(1);
      value=jedis.get(key);
    }catch {
      case e: Throwable => {
        pool.returnBrokenResource(jedis);
        //TODO 输出日志
      }
    }finally {
      RedisConnection.returnResource(pool, jedis);
    }
    value
  }

  /**
   * incr ++1
   */
  def incr(key:String)={
    var jedis:Jedis=null;
    val pool = RedisConnection.getReidsPool();
    try {
      jedis = pool.getResource();
      jedis.select(1);
      jedis.incr(key)
    }catch {
      case e: Throwable => {
        pool.returnBrokenResource(jedis);
        //TODO 输出日志
      }
    }finally {
      RedisConnection.returnResource(pool, jedis);
    }
  }

  /**
   *  Set connection add
   * @param key
   * @param members
   * @return
   */
  def sadd(key:String,members:String)={
    var jedis:Jedis=null;
    val pool = RedisConnection.getReidsPool();
    try {
      jedis = pool.getResource();
      jedis.select(1);
      jedis.sadd(key, members);
    }catch {
      case e: Throwable => {
        pool.returnBrokenResource(jedis);
        //TODO 输出日志
      }
    } finally {
      RedisConnection.returnResource(pool, jedis);
    }
  }

  /**
   * set keys expire 86400
   */
  def setKeyExpire(key:String)={
      var jedis:Jedis=null;
    val pool = RedisConnection.getReidsPool();
      try {
        jedis = pool.getResource();
        jedis.select(1);
        jedis.expire(key, 86400)
      }catch {
        case e: Throwable => {
          pool.returnBrokenResource(jedis);
          //TODO 输出日志
        }
      }finally {
        RedisConnection.returnResource(pool, jedis);
      }
  }

  /**
   * pattern keys
   * @param pattern
   * @return
   */
  def  getKeys(pattern:String):Set[String]={
    var result:Set[String]=null;
    var jedis:Jedis=null;
    val pool = RedisConnection.getReidsPool();
    try {
      jedis = pool.getResource();
      jedis.select(1);
      result = jedis.keys(pattern);
    }catch {
      case e: Throwable => {
        pool.returnBrokenResource(jedis);
        //TODO 输出日志
      }
    } finally {
      RedisConnection.returnResource(pool, jedis);
    }
    result;
  }

  /**
   * delete key
   * @param key
   * @return
   */
  def delKey(key:String)={
    var jedis:Jedis=null;
    val pool = RedisConnection.getReidsPool();
    try {
      jedis = pool.getResource();
      jedis.select(1);
      jedis.del(key)
    }catch {
      case e: Throwable => {
        pool.returnBrokenResource(jedis);
        //TODO 输出日志
      }
    }finally {
      RedisConnection.returnResource(pool, jedis);
    }
  }


  def sdiff(newKey:String,oldKey:String):Set[String]={
    var jedis:Jedis=null;
    var  result:Set[String]=null
    val pool = RedisConnection.getReidsPool();
    try {
      jedis = pool.getResource();
      jedis.select(1);
      result=jedis.sdiff(newKey,oldKey);
    }catch {
      case e: Throwable => {
        pool.returnBrokenResource(jedis);
        //TODO 输出日志
      }
    }finally {
      RedisConnection.returnResource(pool, jedis);
    }
    result
  }
}
