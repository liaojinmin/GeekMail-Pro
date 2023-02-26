package me.geek.mail.api.event

import org.bukkit.entity.Player
import taboolib.platform.type.BukkitProxyEvent


class MailBindEvent(val player: Player, val mail: String) : BukkitProxyEvent() {
}