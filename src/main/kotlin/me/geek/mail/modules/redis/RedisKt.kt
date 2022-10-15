package me.geek.mail.modules.redis

import me.geek.mail.GeekMail
import me.geek.mail.scheduler.redis.Redis

/**
 * 作者: 老廖
 * 时间: 2022/10/16
 *
 **/
class RedisKt(
    override val host: String,
    override val port: Int,
    override val password: String,
    override val ssl: Boolean,
    override val timeout: Int
) : Redis() {
    init {
        onStart()
    }

    override fun processMessage(msg: String) {
       val m = msg.split(":")
        if (m[0] == this.CHANNEL && m.size >= 3) {
            GeekMail.say("收到 Redis 服务器消息！")
            GeekMail.say("服务器: ${m[1]}")
            GeekMail.say("消息: ${m[2]}")
            GeekMail.say("------------------")
        }
    }
}