package me.geek.mail.api.event

import me.geek.mail.api.data.PlayerData
import org.bukkit.entity.Player
import taboolib.platform.type.BukkitProxyEvent

/**
 * 作者: 老廖
 * 时间: 2022/10/3
 *
 **/
class NewPlayerJoinEvent(val player: Player, val data: PlayerData) : BukkitProxyEvent()