package me.geek.mail.scheduler.redis

import me.geek.mail.api.data.PlayerData
import me.geek.mail.api.mail.MailSub
import me.geek.mail.common.market.Item
import me.geek.mail.scheduler.toByteArray
import me.geek.mail.scheduler.toMailSub
import me.geek.mail.scheduler.toMarketData
import me.geek.mail.scheduler.toPlayerData
import org.bukkit.Bukkit
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig
import redis.clients.jedis.JedisPubSub
import taboolib.common.platform.function.submitAsync
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

    val logger: Logger = Logger.getLogger("GeekMail")

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
    val division = "‖"

    abstract fun redisMessage(msg: String)

    override fun onStart() {
        submitAsync {
            getRedisConnection().use {
                if (it.isConnected) {
                    logger.log(Level.INFO, "§7成功启用 Redis 服务器")
                } else {
                    logger.log(Level.WARNING, "§7无法与 Redis 服务器建立连接")
                    return@submitAsync
                }
                it.subscribe( object : JedisPubSub() {
                    override fun onMessage(channel: String, message: String) {
                        if (channel != CHANNEL) return
                        redisMessage(message)
                    }
                }, CHANNEL)
            }
        }
    }




    /**********************  玩家  *************************/
    fun sendOnPlayer(server: String = Bukkit.getPort().toString(), @NotNull targetUid: String) {
        //getRedisConnection().use {
            //it.sadd()
        //}
    }
    /**
     * 发送玩家数据流
     * @param Clazz 要储存的玩家数据类
     */
    fun setPlayerData(@NotNull Clazz: PlayerData) {
        getRedisConnection().use {
            it.setex(Clazz.uuid.toString().toByteArray(),15, Clazz.toByteArray())
        }
    }

    /**
     * 获取玩家数据流
     * @param targetUid 目标Uid
     */
    fun getPlayerData(@NotNull targetUid: String): PlayerData? {
        return getRedisConnection().use { it.get(targetUid.toByteArray())?.toPlayerData() }
    }




    /**********************  邮件  *************************/
    /**
     * 发布订阅消息 通知集群 (用于跨服邮件)
     */
    override fun sendCrossMailPublish(@NotNull server: String, @NotNull messageType: RedisMessageType, @NotNull targetUid: String, @NotNull MailUUid: String) {
        getRedisConnection().use {
            it.publish(CHANNEL, "$server$division$messageType$division$targetUid$division$MailUUid")
        }
    }
    /**
     * 设置邮件数据流
     */
    fun setMailData(@NotNull Clazz: MailSub) {
        getRedisConnection().use {
            it.setex(Clazz.mailID.toString().toByteArray(), 15, Clazz.toByteArray())
        }
    }

    /**
     * 获取邮件数据流
     */
    @Nullable
    fun getMailData(@NotNull MailUid: String): MailSub? {
        return getRedisConnection().use {
            it.get(MailUid.toByteArray())?.toMailSub()
        }
    }


    /**********************  市场  *************************/
    /**
     * 发送跨服商品上架消息
     */
    override fun sendMarketPublish(@NotNull server: String, @NotNull messageType: RedisMessageType, @NotNull PackUid: String) {
        getRedisConnection().use {
            it.publish(CHANNEL, "$server$division$messageType$division$PackUid")
        }
    }
    /**
     * 设置商品数据
     */
    fun setMarketData(@NotNull item: Item) {
        getRedisConnection().use {
            it.setex(item.packUid.toString().toByteArray(), 20, item.toByteArray())
        }
    }
    /**
     * 获取商品数据
     */
    fun getMarketData(@NotNull packUid: String): Item? {
      return getRedisConnection().use {
            it.get(packUid.toByteArray())?.toMarketData()
        }
    }


}

