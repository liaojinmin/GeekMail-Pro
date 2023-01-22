package me.geek.mail.api.event

import me.geek.mail.api.mail.MailSub
import taboolib.platform.type.BukkitProxyEvent

/**
 * 作者: 老廖
 * 时间: 2022/9/7
 *
 **/
class MailSenderEvent(val MailData: MailSub) : BukkitProxyEvent()