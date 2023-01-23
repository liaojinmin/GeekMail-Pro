package me.geek.mail.common.settings

import me.geek.mail.common.settings.config.ItemFilter
import me.geek.mail.common.settings.mail.MailIcon
import me.geek.mail.common.settings.market.MarketData
import me.geek.mail.common.settings.redis.RedisData
import me.geek.mail.common.settings.smtp.SmtpData
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
    val filter: ItemFilter,
    val marker: MarketData,
    val mailIcon: MailIcon,
)