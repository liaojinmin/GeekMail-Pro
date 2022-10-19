package me.geek.mail.modules.redis

import me.geek.mail.GeekMail
import me.geek.mail.common.data.SqlManage
import me.geek.mail.modules.settings.sub.redis.RedisData
import me.geek.mail.scheduler.redis.Redis
import org.bukkit.Bukkit

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


    override fun processMessage(msg: String) {
       val m = msg.split("‖")
        if (m.size >= 2 && m[0] != server) {
            GeekMail.debug("------------------")
            GeekMail.debug("加载缓存")
            SqlManage.addMessage(m[1], m[2])
            GeekMail.debug("服务器: ${m[0]}")
            GeekMail.debug("玩家UUID: ${m[1]}")
            GeekMail.debug("消息: ${m[2].substring(0, 32)} 大小: ${m[2].length}")
            GeekMail.debug("------------------")
        }
    }

}