package me.geek.mail.scheduler.sql

/**
 * 作者: 老廖
 * 时间: 2022/10/12
 *
 **/
data class HikariData(
    val maximum_pool_size: Int = 10,
    val minimum_idle: Int = 10,
    val maximum_lifetime: Int = 1800000,
    val keepalive_time: Int = 0,
    val connection_timeout: Int = 5000,
)