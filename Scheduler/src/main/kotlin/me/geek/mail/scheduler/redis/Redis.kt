package me.geek.mail.scheduler.redis

import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig
import redis.clients.jedis.JedisPubSub
import java.util.logging.Level
import java.util.logging.Logger

/**
 * 作者: 老廖
 * 时间: 2022/10/15
 *
 **/
abstract class Redis: RedisApi {

    var isActive: Boolean = false
        private set
    val logger = Logger.getLogger("GeekMail")

    private lateinit var jedisPool: JedisPool
    val CHANNEL = "GeekMail"

    abstract fun processMessage(msg: String)


    private fun getRedisConnection() : Jedis {
        return this.jedisPool.resource
    }

    fun onStart() {

        if (this.password.isEmpty()) {
            val poolConfig = JedisPoolConfig()
            poolConfig.maxTotal = 10
            this.jedisPool = JedisPool(poolConfig, this.host, this.port, this.timeout, this.ssl)
        } else {
            this.jedisPool = JedisPool(JedisPoolConfig(), this.host, this.port, this.timeout, this.password, this.ssl)
        }
        Thread {
            getRedisConnection().run {
                if (this.isConnected) {
                    isActive = true
                    logger.log(Level.INFO, "§7成功启用 Redis 服务器")
                } else {
                    isActive = false
                    logger.log(Level.WARNING, "§7无法与 Redis 服务器建立连接")
                    return@run
                }
                this.subscribe( object : JedisPubSub() {
                    override fun onMessage(channel: String, message: String) {
                        if (channel != CHANNEL) return
                        processMessage(message)
                    }
                }, CHANNEL)
            }
        }.also {
            it.name = "GeekMail Redis"
        }.start()
    }
    fun send(msg: String, server: String) {
        getRedisConnection().run {
            this.publish(CHANNEL, "$CHANNEL:$server:$msg")
        }
    }

}
