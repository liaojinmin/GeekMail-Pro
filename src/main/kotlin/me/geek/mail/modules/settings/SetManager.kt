package me.geek.mail.modules.settings

import me.geek.mail.modules.settings.sub.config.ItemFilter
import me.geek.mail.modules.settings.sub.redis.RedisData
import me.geek.mail.modules.settings.sub.smtp.SmtpData
import me.geek.mail.scheduler.sql.SqlConfig

/**
 * 作者: 老廖
 * 时间: 2022/10/12
 *
 **/
data class SetManager(
    val SqlData: SqlConfig,
    val SmtpData: SmtpData,
    val redisData: RedisData,
    val filter: ItemFilter
)