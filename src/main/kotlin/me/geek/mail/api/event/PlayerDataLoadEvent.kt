package me.geek.mail.api.event

import me.geek.mail.api.data.PlayerData
import taboolib.platform.type.BukkitProxyEvent

/**
 * 作者: 老廖
 * 时间: 2023/1/21
 *
 **/
class PlayerDataLoadEvent(val data: PlayerData) : BukkitProxyEvent()