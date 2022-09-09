package me.geek.mail.common.listener

import me.geek.mail.common.menu.Menu
import me.geek.mail.common.menu.Menu.openMenu
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent


object MenuOpen {

    @SubscribeEvent(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onCommand(e: PlayerCommandPreprocessEvent) {
        val message = e.message.removePrefix("/")
        if (message.isNotBlank()) {
            Menu.getMenuCommand(message)?.let {
                e.isCancelled = true
                e.player.openMenu(it)
            }
        }
    }
}