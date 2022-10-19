package me.geek.mail.api.mail.event

import me.geek.mail.common.data.MailPlayerData
import org.bukkit.entity.Player
import taboolib.platform.type.BukkitProxyEvent

/**
 * 作者: 老廖
 * 时间: 2022/10/3
 *
 **/
class NewPlayerJoinEvent(val player: Player, val data: MailPlayerData) : BukkitProxyEvent()