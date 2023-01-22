package me.geek.mail.api.event

import taboolib.platform.type.BukkitProxyEvent

/**
 * 作者: 老廖
 * 时间: 2022/9/10
 * 当发送真实邮件通知时唤起事件
 *
 **/
class WebMailSenderEvent(val targetMail: String, val title: String, val text: String, val app: String, val PlayerName: String) : BukkitProxyEvent()
