package me.geek.mail.modules.redis

import me.geek.mail.GeekMail
import me.geek.mail.api.mail.MailSub
import me.geek.mail.common.data.SqlManage
import me.geek.mail.modules.settings.sub.redis.RedisData
import me.geek.mail.scheduler.redis.Redis
import me.geek.mail.scheduler.redis.RedisMessageType
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

    // m[0] = server  m[1] = type  m[2] = targetUid
    override fun processMessage(msg: String) {
        submitAsync {
            val m = msg.split(division)
            GeekMail.debug("Redis message ${m[0]}")
            if (m.size >= 2 && m[0] != server) {
                when (RedisMessageType.valueOf(m[1])) {
                    RedisMessageType.PLAYER_CROSS_SERVER -> SqlManage.addMessage(m[2], m[0])
                    RedisMessageType.CROSS_SERVER_MAIL -> crossServerMail(m[0], m[2])
                }

                GeekMail.debug("------[Debug]------")
                GeekMail.debug("服务器: ${m[0]}")
                GeekMail.debug("玩家UUID: ${m[2]}")
                GeekMail.debug("消息种类: ${m[1]}")
                GeekMail.debug("-------------------")
            }
        }
    }

    /**
     * 跨服 发送邮件处理
     * @param redisKey 一个包含发送端和目标Uid的Redis key
     */
    private fun crossServerMail(server: String, targetUid: String) {
        // 直接获取玩家，如果 null 说明玩家不在这个服务器
        Bukkit.getPlayer(UUID.fromString(targetUid))?.let {
            val clazz = this.getMailData(server, targetUid, MailSub::class.java)
            if (clazz is MailSub) {
                clazz.sendMail()
                this.remMailData(server, targetUid)
            }
        }
    }


}