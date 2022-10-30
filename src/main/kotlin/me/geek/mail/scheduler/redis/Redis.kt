package me.geek.mail.scheduler.redis

import me.geek.mail.api.mail.MailSub
import me.geek.mail.common.data.MailPlayerData
import me.geek.mail.common.market.Item
import me.geek.mail.modules.Mail_Item
import me.geek.mail.modules.Mail_Normal
import me.geek.mail.utils.classSerializable
import me.geek.mail.utils.classUnSerializable
import me.geek.mail.utils.serializeItemStacks
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

    abstract fun processMessage(msg: String)

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
                        processMessage(message)
                    }
                }, CHANNEL)
            }
        }
    }




    /**
     * 发送玩家数据流
     * @param Clazz 要储存的玩家数据类
     */
    fun setPlayerData(@NotNull Clazz: MailPlayerData) {
        Clazz.mailData.forEach {
            when (it) {
                is Mail_Item -> it.itemStackString = it.itemStacks.serializeItemStacks()
                is Mail_Normal -> it.itemStackString = it.itemStacks.serializeItemStacks()
            }
        }
        getRedisConnection().use {
            it.setex(Clazz.uuid.toString().toByteArray(),15, Clazz.classSerializable())
        }
    }

    /**
     * 获取玩家数据流
     * @param targetUid 目标Uid
     */
    fun getPlayerData(@NotNull targetUid: String): Any? {
        return getRedisConnection().use {
            it.get(targetUid.toByteArray())?.classUnSerializable(MailPlayerData::class.java, true)
        }
    }





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
            it.setex(Clazz.mailID.toString().toByteArray(), 15, Clazz.classSerializable())
        }
    }

    /**
     * 获取邮件数据流
     */
    @Nullable
    fun getMailData(@NotNull MailUid: String, @NotNull Clazz: Class<*>): Any? {
        return getRedisConnection().use {
            it.get(MailUid.toByteArray())?.classUnSerializable(Clazz, false)
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
            it.setex(item.packUid.toString().toByteArray(), 20, item.classSerializable())
        }
    }
    /**
     * 获取商品数据
     */
    fun getMarketData(@NotNull packUid: String): Any? {
      return getRedisConnection().use {
            it.get(packUid.toByteArray())?.classUnSerializable(Item::class.java)
        }
    }


}

