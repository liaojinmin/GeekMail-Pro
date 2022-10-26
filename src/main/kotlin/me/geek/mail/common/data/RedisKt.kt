package me.geek.mail.common.data

import me.geek.mail.GeekMail
import me.geek.mail.api.mail.MailSub
import me.geek.mail.modules.settings.redis.RedisData
import me.geek.mail.scheduler.redis.Redis
import me.geek.mail.scheduler.redis.RedisMessageType
import me.geek.mail.scheduler.redis.RedisMessageType.*
import org.bukkit.Bukkit
import taboolib.common.platform.function.submitAsync
import java.util.*

/**
 * 作者: 老廖
 * 时间: 2022/10/16
 *
 **/
class RedisKt(data: RedisData) : Redis() {

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
    override fun processMessage(msg: String) {
        submitAsync {
            val m = msg.split(division)
            GeekMail.debug("Redis message ${m[0]}")
            if (m.size == 4 && m[0] != server) { // 防止本服发送本服处理
                when (valueOf(m[1])) {
                    PLAYER_CROSS_SERVER -> TODO()
                    CROSS_SERVER_MAIL -> crossServerMail(m[3], m[2])
                    MARKET_ADD -> TODO()
                    MARKET_REM -> TODO()
                }
                GeekMail.debug("------[Debug]------")
                GeekMail.debug("服务器: ${m[0]}")
                GeekMail.debug("消息种类: ${m[1]}")
                GeekMail.debug("目标Uid: ${m[2]}")
                GeekMail.debug("邮件Uid: ${m[3]}")
                GeekMail.debug("-------------------")
            }
        }
    }


    private fun crossServerMail(MailUUID: String, targetUid: String) {
        // 直接获取玩家，如果 null 说明玩家不在这个服务器
        Bukkit.getPlayer(UUID.fromString(targetUid))?.let {
            val clazz = this.getMailData(MailUUID, MailSub::class.java)
            if (clazz is MailSub) {
                clazz.sendMail()
            }
        }
    }


}