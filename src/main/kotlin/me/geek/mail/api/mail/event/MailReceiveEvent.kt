package me.geek.mail.api.mail.event

import me.geek.mail.api.mail.MailSub
import taboolib.platform.type.BukkitProxyEvent

/**
 * GeekMail-Pro
 * me.geek.mail.api.mail.event.MailReceiveEvent
 *
 * @author xiaomu
 * @since 2022/9/28 8:26 AM
 */
class MailReceiveEvent(val mailSub: MailSub) : BukkitProxyEvent() {
}