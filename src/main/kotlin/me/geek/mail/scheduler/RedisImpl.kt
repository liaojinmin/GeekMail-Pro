package me.geek.mail.scheduler

import me.geek.mail.api.mail.MailSub
import me.geek.mail.common.market.Market
import me.geek.mail.scheduler.redis.Redis
import me.geek.mail.scheduler.redis.RedisData
import me.geek.mail.scheduler.redis.RedisMessageType.*
import org.bukkit.Bukkit
import java.util.*

/**
 * 作者: 老廖
 * 时间: 2022/10/16
 *
 **/
class RedisImpl(data: RedisData) : Redis() {

    init {
        this.host = data.host
        this.port = data.port
        this.password = data.password
        this.ssl = data.ssl
        onStart()
    }

    private val server = Bukkit.getServer().port.toString()

    // m[0] = server  m[1] = type  m[2] = targetUid  m[3] = MailUid
    // 主要用于接收跨服邮件
    override fun redisMessage(msg: String) {
        //submitAsync {
            val m = msg.split(division)
            if (m.size >= 3 && m[0] != server) { // 防止本服发送本服处理
                when (valueOf(m[1])) {
                    PLAYER_CROSS_SERVER -> TODO()
                    CROSS_SERVER_MAIL -> crossServerMail(m[3], m[2])
                    MARKET_ADD -> crossAddMarket(m[2])
                    MARKET_REM -> crossRemMarket(m[2])
                    PLAYER_QUERY -> TODO()
                    PLAYER_INLINE -> TODO()
                }
                /*
                GeekMail.debug("Redis message ${m[0]}")
                GeekMail.debug("------[Debug]------")
                GeekMail.debug("服务器: ${m[0]}")
                GeekMail.debug("消息种类: ${m[1]}")
                GeekMail.debug("-------------------")

                 */
            }
       // }
    }


    private fun crossServerMail(MailUUID: String, targetUid: String) {
        // 直接获取玩家，如果 null 说明玩家不在这个服务器
        Bukkit.getPlayer(UUID.fromString(targetUid))?.let {
            val clazz = this.getMailData(MailUUID)
            if (clazz is MailSub) {
                clazz.sendMail()
            }
        }
    }
    private fun crossAddMarket(packUid: String) {
        this.getMarketData(packUid)?.let {
            Market.addMarketItem(it)
        }
    }
    private fun crossRemMarket(packUid: String) {
        Market.remMarketItem(UUID.fromString(packUid), false)
    }



}