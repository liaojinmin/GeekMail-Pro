package me.geek.mail.settings

import me.geek.mail.scheduler.redis.RedisData
import me.geek.mail.scheduler.sql.SqlConfig
import me.geek.mail.settings.config.ItemFilter
import me.geek.mail.settings.mail.MailIcon
import me.geek.mail.settings.market.MarketData
import me.geek.mail.settings.smtp.SmtpData

/**
 * 作者: 老廖
 * 时间: 2022/10/12
 *
 **/
data class SetManager(
    val SqlData: SqlConfig,
    val SmtpData: SmtpData,
    val redisData: RedisData,
    val filter: ItemFilter,
    val marker: MarketData,
    val mailIcon: MailIcon,
)