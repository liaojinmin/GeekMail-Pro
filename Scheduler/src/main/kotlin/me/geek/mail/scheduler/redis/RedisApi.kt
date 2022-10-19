package me.geek.mail.scheduler.redis

import org.jetbrains.annotations.NotNull

/**
 * 作者: 老廖
 * 时间: 2022/10/15
 *
 **/
interface RedisApi {
    val host: String // IP地址
    val port: Int // 端口
    val ssl: Boolean // 是否使用SSL
    val timeout: Int // 超时时间
    val password: String // 密码

    fun onStart()

    fun sendPublish(@NotNull server: String, @NotNull UUID: String, @NotNull msg: String)
}
