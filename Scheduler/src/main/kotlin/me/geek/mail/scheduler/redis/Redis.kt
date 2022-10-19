package me.geek.mail.scheduler.redis

import org.jetbrains.annotations.NotNull
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
    override var host: String = "127.0.0.1"
    override var port: Int = 6379
    override var password: String = ""
    override var ssl: Boolean = false
    override var timeout: Int = 5000

    private val logger: Logger = Logger.getLogger("GeekMail")

    private val jedisPool by lazy {
        if (this.password.isEmpty()) {
            val poolConfig = JedisPoolConfig()
            poolConfig.maxTotal = 10
            JedisPool(poolConfig, this.host, this.port, this.timeout, this.ssl)
        } else {
            JedisPool(JedisPoolConfig(), this.host, this.port, this.timeout, this.password, this.ssl)
        }
    }

    private fun getRedisConnection() : Jedis {
        return this.jedisPool.resource
    }

    val CHANNEL = "GeekMail"

    abstract fun processMessage(msg: String)

    override fun onStart() {
        Thread { getRedisConnection().action {
                if (this.isConnected) {
                    logger.log(Level.INFO, "§7成功启用 Redis 服务器")
                } else {
                    logger.log(Level.WARNING, "§7无法与 Redis 服务器建立连接")
                    return@action
                }
                this.subscribe( object : JedisPubSub() {
                    override fun onMessage(channel: String, message: String) {
                        if (channel != CHANNEL) return
                        processMessage(message)
                    }
                }, CHANNEL)
            }
        }.also { it.name = "GeekMail Redis" }.start()
    }


    override fun sendPublish(@NotNull server: String, @NotNull UUID: String , @NotNull msg: String) {
        getRedisConnection().action {
            this.publish(CHANNEL, "$server‖$UUID‖$msg")
        }
    }

    fun toBytes(@NotNull Clazz: Any): ByteArray {
        return Clazz.classSerializable()
    }
    fun toClass(@NotNull data: ByteArray): Any {
        return data.classUnSerializable()
    }

    fun setClassData(@NotNull server: String, @NotNull UUID: String, @NotNull Clazz: Any) {
        getRedisConnection().set("$server:$UUID".toByteArray(), Clazz.classSerializable())
    }
    fun getClassData(@NotNull server: String, @NotNull UUID: String): Any? {
        val data = getRedisConnection().get("$server:$UUID".toByteArray())
        return if (data.equals("nil")) null else data.classUnSerializable()
    }

}

