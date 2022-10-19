package me.geek.mail.scheduler.redis

import me.geek.mail.serializable.SerializeUtil
import redis.clients.jedis.Jedis

/**
 * 作者: 老廖
 * 时间: 2022/10/16
 *
 **/
fun <T: Jedis, R> T.action(func: Jedis.(T) -> R) {
    func(this)
}
fun Any.classSerializable(): ByteArray {
    return SerializeUtil.serialize(this)
}
fun ByteArray.classUnSerializable() : Any {
    return SerializeUtil.deserialize(this)
}
fun ByteArray.toHexString(): String {
    return SerializeUtil.toHexString(this)
}
fun String.toByteArrays(): ByteArray {
    return SerializeUtil.toByteArray(this)
}