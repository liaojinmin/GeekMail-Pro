package me.geek.mail.api.mail.event

import org.bukkit.entity.Player
import taboolib.platform.type.BukkitProxyEvent

/**
 * GeekMail-Pro
 * me.geek.mail.api.mail.event.MailReceiveEvent
 *
 * @author xiaomu
 * @since 2022/9/28 8:26 AM
 */
class MailReceiveEvent(val player: Player, val sender: Player, val title: String) : BukkitProxyEvent() {
}