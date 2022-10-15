package me.geek.mail.scheduler.redis

import redis.clients.jedis.Jedis

/**
 * 作者: 老廖
 * 时间: 2022/10/16
 *
 **/
fun <T: Jedis, R> T.run(func: Jedis.(T) -> R) {
    try {
        func(this)
    } catch (ex: Exception) {
        throw ex
    }
}