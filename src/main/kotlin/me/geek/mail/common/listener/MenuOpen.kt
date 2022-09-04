package me.geek.mail.common.listener

import me.geek.mail.GeekMail
import me.geek.mail.common.Menu.MAction
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent


object MenuOpen {

    @SubscribeEvent(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onCommand(e: PlayerCommandPreprocessEvent) {
        val message = e.message.removePrefix("/")
        if (message.isNotBlank()) {
            if (GeekMail.menu.getMenuCommand(message) != null) {
                val player = e.player
                val m = GeekMail.menu.getMenuCommand(message)
                MAction(player, GeekMail.menu.getMenuTag(m), GeekMail.menu.Build(player, m))
                e.isCancelled = true
                return
            }
        }
    }
}